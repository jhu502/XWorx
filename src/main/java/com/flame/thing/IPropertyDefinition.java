package com.flame.thing;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.orm.XPersistable;
import com.flame.localize.ILocalization;
import com.flame.type.XBaseType;

public interface IPropertyDefinition extends ILocalization, XPersistable {

	public String getName();

	public void setName(String name);

	public String getDescription();

	public void setDescription(String description);

	public XBaseType getBaseType();

	public void setBaseType(XBaseType baseType);

	public PropertyType getPropertyType();

	public void setPropertyType(PropertyType propertyType);

	@JsonIgnore
	public Class<?> getPropertyClass();

	public int getOrdinal();

	public void setOrdinal(int ordinal);

	public String getDefaultValue();

	public void setDefaultValue(String defaultValue);

	public boolean isNullable();

	public String getNullableImg();

	public void setNullable(boolean nullable);

	public boolean isPersistent();

	/**
	 * Easyui的Datagrid如果需要提示信息，就需要加上class='easyui-tooltip'，然后title被作为提示信息
	 */
	public String getPersistentImg();

	public void setPersistent(boolean persistent);

	public boolean isReadOnly();

	public String getReadOnlyImg();

	public void setReadOnly(boolean readOnly);

	public boolean isLogged();

	public String getLoggedImg();

	public void setLogged(boolean logged);

	public boolean isBuildIn();

	public void setBuildIn(boolean buildIn);

	public String getSource();

	public void setSource(String source);

	@JsonIgnore
	public List<IPropertyConstraint> getPropertyConstraints();

	public void setPropertyConstraints(List<IPropertyConstraint> propertyConstraints);

	@JsonIgnore
	public IPropertyProvider getPropertyProvider();

	public void setPropertyProvider(IPropertyProvider propertyProvider);

	@Override
	default String getDisplay() {
		return this.getOid();
	}
}