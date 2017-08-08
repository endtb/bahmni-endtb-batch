SELECT obs0.obs_id,obs${input.form.depthToParent}.obs_id as parent_obs_id
FROM obs obs0
<#if input.form.depthToParent &gt; 0>
<#list 1..input.form.depthToParent as x>
INNER JOIN obs obs${x} on ( obs${x}.obs_id=obs${x-1}.obs_group_id and obs${x}.voided=0 )
</#list>
</#if>
WHERE obs0.concept_id=${input.form.formName.id?c}
AND obs0.voided = 0
<#if input.form.parent?has_content>
AND obs${input.form.depthToParent}.concept_id=${input.form.parent.formName.id?c}
</#if>
<#if input.restrictByTreatmentInitiationCohort>
AND obs0.person_id in
(select distinct person_id from obs where concept_id in (SELECT concept_id from concept_name where name='Treatment Initiation')  and obs_group_id IS NULL and voided=0)
</#if>



