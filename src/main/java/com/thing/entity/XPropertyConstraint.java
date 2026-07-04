package com.thing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.flame.orm.XObject;
import com.flame.thing.ConstraintType;
import com.flame.thing.IPropertyConstraint;
import com.flame.thing.IPropertyDefinition;

/**
 * @author ph
 * @version 1.0
 * @created 29-10月-2019 22:20:05
 */
@Entity
@Table(name = "XPropertyConstraint", uniqueConstraints = {})
public class XPropertyConstraint extends XObject implements IPropertyConstraint {
	private static final long serialVersionUID = 1L;
	@Column(name = "constraintType")
	@Enumerated(EnumType.STRING)
	private ConstraintType constraintType;
	@Column(name = "constraintValue")
	private String constraintValue;
	@ManyToOne(targetEntity = XPropertyDefinition.class)
	@JoinColumn(name = "definitionId", foreignKey = @ForeignKey(name = "PROPERTY_DEFINITION_ID_FK"))  //@JoinColumn中指定的name是Many实体的外键列
	private IPropertyDefinition propertyDefinition;

	public ConstraintType getConstraintType() {
		return constraintType;
	}

	public void setConstraintType(ConstraintType constraintType) {
		this.constraintType = constraintType;
	}

	public String getConstraintValue() {
		return constraintValue;
	}

	public void setConstraintValue(String constraintValue) {
		this.constraintValue = constraintValue;
	}

	public IPropertyDefinition getPropertyDefinition() {
		return propertyDefinition;
	}

	public void setPropertyDefinition(IPropertyDefinition propertyDefinition) {
		this.propertyDefinition = propertyDefinition;
	}
}