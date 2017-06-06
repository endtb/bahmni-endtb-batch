package org.bahmni.batch.form.domain;

import java.util.Date;

public class Obs {
	private String treatmentNumber;
	private Integer id;
	private Integer parentId;
	private Concept field;
	private String value;
	private Date dateCreated;
	private Date dateChanged;   // note that Obs cannot change (they are voided and recreated, but in this construct it's possible for the treatment number to have changed)

	public Obs(){}

	public Obs(String treatmentNumber, Integer id, Integer parentId, Concept field, String value) {
		this.treatmentNumber = treatmentNumber;
		this.id = id;
		this.parentId = parentId;
		this.field = field;
		this.value = value;
	}

	public Obs(String treatmentNumber, Integer id, Integer parentId, Concept field, String value, Date dateCreated, Date dateChanged) {
		this.treatmentNumber = treatmentNumber;
		this.id = id;
		this.parentId = parentId;
		this.field = field;
		this.value = value;
		this.dateCreated = dateCreated;
		this.dateChanged = dateChanged;
	}

	public String getTreatmentNumber() {
		return treatmentNumber;
	}

	public void setTreatmentNumber(String treatmentNumber) {
		this.treatmentNumber = treatmentNumber;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Concept getField() {
		return field;
	}

	public void setField(Concept field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateChanged() {
		return dateChanged;
	}

	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}
}
