package com.thing.entity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.util.StringUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.type.ServiceType;
import com.flame.annotations.XDefinition;
import com.flame.annotations.XService;
import com.flame.localize.AbstractLocalization;
import com.flame.thing.IModelManaged;
import com.flame.thing.IPropertyDefinition;
import com.flame.thing.IServiceDefinition;
import com.flame.thing.IThingModel;
import com.flame.type.XBaseType;
import com.flame.util.FlameUtils;
import com.flame.util.XException;
import com.flame.vc.Master;
import com.thing.common.DefaultThing;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import xw.content.IContentHolder;

/**
 * number: 采用反域名命名规则, e.g.: com.flame.ManufacturerPart
 */
@Entity
@Table(name = "XThingModel", uniqueConstraints = {})
@XDefinition(name = "XThingModel", config = DefaultThing.class, icon = "images/body/tmodel.png", description = "XThingModel", display = "Thing Model", en_US = "Thing Model", zh_CN = "Thing模型")
public class XThingModel extends AbstractLocalization implements IThingModel, IContentHolder, IModelManaged {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "number", nullable = false)
	private String number = "";
	@Basic
	@Column(name = "name", nullable = false)
	private String name = "";
	@Basic
	@Column(name = "icon", length = 100)
	private String icon;
	@Basic
	@Column(name = "description", length = 600)
	private String description;
	@Basic
	@Column(name = "entity", length = 200)
	private String entity;
	private transient Class<?> entityClass;
	@Basic
	@Column(name = "model", length = 200)
	private String model;
	private transient Class<?> modelClass;
	@Basic
	@Column(name = "pageUri", length = 500)
	private String pageUri;
	@ManyToOne(targetEntity = XThingModel.class)
	@JoinColumn(name = "modelId", foreignKey = @ForeignKey(name = "THINGMODEL_FK"))
	private IThingModel thingModel;
	@OneToMany(targetEntity = XPropertyDefinition.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "providerId") // @JoinColumn中指定的name是Many实体的外键列
	private List<IPropertyDefinition> propertyDefinitions = new ArrayList<>();
	@OneToMany(targetEntity = XServiceDefinition.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "providerId") // @JoinColumn中指定的name是Many实体的外键列
	private List<IServiceDefinition> serviceDefinitions = new ArrayList<>();
	private transient Map<String, Field> nativeFields = new LinkedHashMap<>();

	@XService(name = "getNumber", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getNumber() {
		return number;
	}

	public String getModelKey() {
		return FlameUtils.getLastSplit(this.getNumber(), ".");
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@XService(name = "getName", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XService(name = "getIcon", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@XService(name = "getDescription", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XService(name = "getPageUri", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getPageUri() {
		return this.pageUri;
	}

	public void setPageUri(String pageUri) {
		this.pageUri = pageUri;
	}

	@XService(name = "getEntity", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getEntity() {
		return entity;
	}

	public Class<?> getEntityClass() {
		if (this.entityClass == null) {
			try {
				this.entityClass = Class.forName(this.entity);
			} catch (ClassNotFoundException e) {
				throw new XException(e);
			}
		}
		return this.entityClass;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	@XService(name = "getModel", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getModel() {
		return this.model;
	}

	public Class<?> getModelClass() {
		if (this.modelClass == null) {
			try {
				this.modelClass = Class.forName(this.model);
			} catch (ClassNotFoundException e) {
				throw new XException(e);
			}
		}
		return this.modelClass;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@JsonIgnore
	@Override
	public List<IPropertyDefinition> getPropertyDefinitions() {
		return propertyDefinitions;
	}

	@JsonIgnore
	@Override
	public IPropertyDefinition getPropertyDefinition(String name) {
		if (propertyDefinitions == null) {
			return null;
		}
		IPropertyDefinition propertyDefinition = null;
		for (IPropertyDefinition item : propertyDefinitions) {
			if (item.getName().equals(name)) {
				propertyDefinition = item;
			}
		}
		return propertyDefinition;
	}

	@JsonIgnore
	@Override
	public List<IServiceDefinition> getServiceDefinitions() {
		return serviceDefinitions;
	}

	@JsonIgnore
	@Override
	public IServiceDefinition getServiceDefinition(String serviceName) {
		if (serviceDefinitions == null) {
			return null;
		}
		IServiceDefinition serviceDefinition = null;
		for (IServiceDefinition item : serviceDefinitions) {
			if (item.getName().equals(serviceName)) {
				serviceDefinition = item;
			}
		}
		return serviceDefinition;
	}

	@JsonIgnore
	@Override
	public IThingModel getThingModel() {
		return this.thingModel;
	}

	@Override
	public void setThingModel(IThingModel thingModel) {
		this.thingModel = thingModel;
	}

	@Override
	public String getThingIdentity() {
		return this.getClass().getSimpleName() + ":" + this.getModelKey();
	}

	@JsonIgnore
	public Field getNativeField(String name) {
		if (FlameUtils.isBlank(name))
			return null;

		Map<String, Field> fieldMap = this.getNativeFields();
		return fieldMap.get(name);
	}

	@JsonIgnore
	public Map<String, Field> getNativeFields() {
		if (this.nativeFields.isEmpty()) {
			Map<String, Field> fieldMap = getORMappingFields(this.getEntityClass());
			for (Field field : fieldMap.values().toArray(new Field[0])) {
				ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
				if (manyToOne != null && "master".equals(field.getName())) {
					Map<String, Field> masterMap = getORMappingFields(field.getType());
					for (Field $field : masterMap.values()) {
						String fieldName = "master." + $field.getName();
						if (!fieldMap.containsKey(fieldName)) {
							fieldMap.put(fieldName, $field);
						}
					}
				}
			}
			this.nativeFields.putAll(fieldMap);
		}

		return this.nativeFields;
	}

	private static Map<String, Field> getORMappingFields(Class<?> clazz) {
		Map<String, Field> fieldMap = new LinkedHashMap<>();
		Class<?> recurseCls = clazz;
		while (recurseCls != null) {
			for (Field field : recurseCls.getDeclaredFields()) {
				if (field.getAnnotation(Basic.class) != null || field.getAnnotation(Enumerated.class) != null || field.getAnnotation(ManyToOne.class) != null
						|| field.getAnnotation(OneToOne.class) != null || field.getAnnotation(OneToMany.class) != null) {
					fieldMap.put(field.getName(), field);
				}
				if ("master".equals(field.getName()) && Master.class.isAssignableFrom(field.getType())) {
					Map<String, Field> masterFields = getORMappingFields(field.getType());
					for (Field masterField : masterFields.values()) {
						if (!fieldMap.containsKey(masterField.getName())) {
							fieldMap.put(masterField.getName(), masterField);
						}
					}
				}
			}
			Class<?> $class = recurseCls.getSuperclass();
			MappedSuperclass superclass = $class.getAnnotation(MappedSuperclass.class);
			if (superclass == null) {
				recurseCls = null;
			} else {
				recurseCls = $class;
			}
		}

		return fieldMap;
	}

	@Override
	public String getLocalDisplay() {
		String local = super.getLocalDisplay();
		if (StringUtil.isBlank(local)) {
			return this.getName();
		} else {
			return local;
		}
	}
}
