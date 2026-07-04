package com.thing.entity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.localize.AbstractLocalization;
import com.flame.localize.LocalizationHelper;
import com.flame.thing.IPropertyConstraint;
import com.flame.thing.IPropertyDefinition;
import com.flame.thing.IPropertyProvider;
import com.flame.thing.IThingModel;
import com.flame.thing.PropertyType;
import com.flame.type.XBaseType;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * @author ph
 * @version 1.0
 * @created 29-10月-2019 22:20:05
 */
@Entity
@Table(name = "XPropertyDefinition", uniqueConstraints = {})
public class XPropertyDefinition extends AbstractLocalization implements IPropertyDefinition {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "name", nullable = false)
	private String name;
	@Basic
	@Column(name = "description")
	private String description;
	@Column(name = "baseType")
	@Enumerated(EnumType.STRING)
	private XBaseType baseType;
	@Column(name = "propertyType")
	@Enumerated(EnumType.STRING)
	private PropertyType propertyType;
	@Basic
	@Column(name = "ordinal")
	private int ordinal;
	@Basic
	@Column(name = "defaultValue")
	private String defaultValue = "";
	@Basic
	@Column(name = "nullable")
	private boolean nullable = true;
	@Basic
	@Column(name = "persistent")
	private boolean persistent = false;
	@Basic
	@Column(name = "readOnly")
	private boolean readOnly = false;
	@Basic
	@Column(name = "logged")
	private boolean logged = false;
	@Basic
	@Column(name = "buildIn")
	private boolean buildIn;
	@Basic
	@Column(name = "source")
	private String source;
	@OneToMany(targetEntity = XPropertyConstraint.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "definitionId")
	private List<IPropertyConstraint> propertyConstraints = new ArrayList<>();
	@ManyToOne(targetEntity = XThingModel.class)
	@JoinColumn(name = "providerId")
	private IPropertyProvider propertyProvider;

	public static XPropertyDefinition newPropertyDefinition(IPropertyProvider model, Field field) {
		XPropertyDefinition definition = new XPropertyDefinition();
		definition.setName(field.getName());
		definition.setPropertyProvider(model);
		XBaseType baseType = XBaseType.toBaseType(field.getType());
		definition.setBaseType(baseType);
		definition.setPropertyType(PropertyType.MBA);
		String display = LocalizationHelper.get(field.getName(), Locale.getDefault());
		display = display == null ? "" : display;
		definition.setDisplay(display);
		String en_US = LocalizationHelper.get(field.getName(), Locale.US);
		en_US = en_US == null ? "" : en_US;
		definition.setEn_US(en_US);
		String zh_CN = LocalizationHelper.get(field.getName(), Locale.CHINA);
		zh_CN = zh_CN == null ? "" : zh_CN;
		definition.setZh_CN(zh_CN);
		definition.setLogged(false);
		definition.setPersistent(true);
		definition.setReadOnly(false);
		Column column = field.getAnnotation(Column.class);
		if (column != null && !column.nullable()) {
			definition.setNullable(false);
		} else {
			definition.setNullable(true);
		}

		return definition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public XBaseType getBaseType() {
		return baseType;
	}

	public void setBaseType(XBaseType baseType) {
		this.baseType = baseType;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
	}

	@Override
	public Class<?> getPropertyClass() {
		IPropertyProvider provider = this.getPropertyProvider();
		if (provider instanceof IThingModel thingModel) {
			Map<String, Field> nativeFields = thingModel.getNativeFields();
			Field field = nativeFields.get(this.getName());
			if (field != null) {
				return field.getType();
			}
		}
		return null;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public String getDefaultValue() {
		return this.defaultValue == null ? "" : this.defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isNullable() {
		return nullable;
	}

	public String getNullableImg() {
		if (!this.isNullable()) {
			return "<img src='images/required.png' title='Not Null' class='easyui-tooltip'/> ";
		} else {
			return "";
		}
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isPersistent() {
		return persistent;
	}

	/**
	 * Easyui的Datagrid如果需要提示信息，就需要加上class='easyui-tooltip'，然后title被作为提示信息
	 */
	public String getPersistentImg() {
		if (this.isPersistent()) {
			return "<img src='images/persistent.png' title='Persistent' class='easyui-tooltip'/> ";
		} else {
			return "";
		}
	}

	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public String getReadOnlyImg() {
		if (this.isReadOnly()) {
			return "<img src='images/readonly.png' title='ReadOnly' class='easyui-tooltip'/> ";
		} else {
			return "";
		}
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isLogged() {
		return logged;
	}

	public String getLoggedImg() {
		if (this.isLogged()) {
			return "<img src='images/logged.png' title='Logged' class='easyui-tooltip'/> ";
		} else {
			return "";
		}
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}

	public boolean isBuildIn() {
		return buildIn;
	}

	public void setBuildIn(boolean buildIn) {
		this.buildIn = buildIn;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@JsonIgnore
	public List<IPropertyConstraint> getPropertyConstraints() {
		return propertyConstraints;
	}

	public void setPropertyConstraints(List<IPropertyConstraint> propertyConstraints) {
		this.propertyConstraints = propertyConstraints;
	}

	@JsonIgnore
	public IPropertyProvider getPropertyProvider() {
		return propertyProvider;
	}

	public void setPropertyProvider(IPropertyProvider propertyProvider) {
		this.propertyProvider = propertyProvider;
	}
}