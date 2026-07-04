package com.flame.thing;

public interface IPropertyConstraint {

	public ConstraintType getConstraintType();

	public void setConstraintType(ConstraintType constraintType);

	public String getConstraintValue();

	public void setConstraintValue(String constraintValue);

	public IPropertyDefinition getPropertyDefinition();

	public void setPropertyDefinition(IPropertyDefinition propertyDefinition);
}