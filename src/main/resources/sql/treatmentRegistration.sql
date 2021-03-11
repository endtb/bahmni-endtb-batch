SELECT
  o.identifier as 'id_emr',
  DATE_FORMAT(o.birthdate, '%d/%b/%Y') as 'dob',
  o.age as 'age',
  IF(o.gender = 'M',1, IF(o.gender = 'F',2, 3)) AS 'sex',
  o.program_name as 'tbregtype',
  MAX(IF(pat.program_attribute_type_id = '2', CONCAT('\"',o.attr_value,'\"'), NULL)) AS `regnum`,
  DATE_FORMAT(o.date_enrolled, '%d/%b/%Y') as 'd_reg',
  MAX(IF(pat.program_attribute_type_id = '6', CONCAT('\"',o.concept_name, '\"'), NULL)) AS `reg_facility`,
  tc.cohort_type AS `cohort_type`,
  tr.regimen_type AS `regimen_type`,
  o.status,
  patient_id,
  o.patient_program_id as `patient_program_id`,
  DATE_FORMAT(MAX(o.date_created),'%Y-%m-%d %H:%i:%S'),
  DATE_FORMAT(MAX(o.date_changed),'%Y-%m-%d %H:%i:%S')
FROM
  (SELECT
     CONCAT('\"',pi.identifier,'\"') as identifier,
     floor(datediff(CURDATE(), p.birthdate) / 365) AS age,
     p.birthdate,
     p.gender,
     CONCAT('\"',prog.name, '\"') as program_name,
     attr.attribute_type_id,
     attr.value_reference as attr_value,
     pp.date_enrolled as date_enrolled,
     pp.patient_id,
     prog.program_id,
     cn.name as concept_name,
     CONCAT('\"',outcome_concept.name, '\"') as status,
     pp.patient_program_id,
     pp.date_created as date_created,
     GREATEST(COALESCE(pp.date_changed, pp.date_created),
              COALESCE(p.date_changed, p.date_created),
              COALESCE(pi.date_changed, pi.date_created),
              COALESCE(attr.date_changed, attr.date_created)
     ) as date_changed
   FROM  patient_program pp
     JOIN program prog ON pp.program_id = prog.program_id AND pp.voided = 0
     JOIN person p ON pp.patient_id = p.person_id
     JOIN person_name pn ON p.person_id = pn.person_id
     JOIN patient pa ON pp.patient_id = pa.patient_id
     JOIN patient_identifier pi ON pa.patient_id = pi.patient_id AND pi.voided=0
     LEFT OUTER JOIN patient_program_attribute attr ON pp.patient_program_id = attr.patient_program_id AND attr.voided = 0
     LEFT OUTER JOIN program_attribute_type attr_type ON attr.attribute_type_id = attr_type.program_attribute_type_id
     LEFT JOIN program_attribute_type pg_at_cohort on (pg_at_cohort.name = 'Belongs to external cohort')
     LEFT JOIN patient_program_attribute ppa_cohort on (pp.patient_program_id = ppa_cohort.patient_program_id and
                                                        pg_at_cohort.program_attribute_type_id =
                                                        ppa_cohort.attribute_type_id)
     LEFT JOIN concept_view ppa_cv on ppa_cv.concept_id = ppa_cohort.value_reference AND ppa_cv.retired IS FALSE
     LEFT OUTER JOIN concept_name cn ON cn.concept_id = attr.value_reference AND cn.voided =0
     LEFT OUTER JOIN concept_name outcome_concept ON outcome_concept.concept_id = pp.outcome_concept_id and outcome_concept.concept_name_type='FULLY_SPECIFIED' AND outcome_concept.voided = 0
   WHERE (:belongsToExternalCohort IS TRUE AND ppa_cv.concept_full_name IN (:externalCohortTypes))
         OR (:belongsToExternalCohort IS FALSE)
  ) o
  LEFT OUTER JOIN
  (
  SELECT patient_program_attribute_id, patient_program_id, (SELECT name FROM concept_name cn WHERE cn.concept_id = value_reference
  AND cn.voided = 0 AND cn.locale = 'en' AND cn.concept_name_type = 'SHORT') cohort_type, attribute_type_id FROM
  patient_program_attribute ppa
  JOIN program_attribute_type pat
  ON ppa.voided = 0 AND ppa.attribute_type_id = program_attribute_type_id AND pat.name = "Belongs to external cohort"
  ) tc ON o.patient_program_id = tc.patient_program_id
  LEFT OUTER JOIN
  (
  SELECT patient_program_attribute_id, patient_program_id, (SELECT name FROM concept_name cn WHERE cn.concept_id = value_reference
  AND cn.voided = 0 AND cn.locale = 'en' AND cn.concept_name_type = 'SHORT') regimen_type, attribute_type_id FROM
  patient_program_attribute ppa
  JOIN program_attribute_type pat
  ON ppa.voided = 0 AND ppa.attribute_type_id = program_attribute_type_id AND pat.name = "Regimen type"
  ) tr ON o.patient_program_id = tr.patient_program_id
  LEFT OUTER JOIN program_attribute_type pat ON o.attribute_type_id = pat.program_attribute_type_id
GROUP BY patient_id, o.patient_program_id
ORDER BY patient_id, date_enrolled;