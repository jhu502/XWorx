package com.thing.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.flame.thing.*;
import org.springframework.stereotype.Service;

import com.flame.annotations.XDefinition;
import com.flame.config.basic.BasicConfiguration;
import com.flame.localize.ILocalization;
import com.flame.orm.PersistenceHelper;
import com.flame.util.XException;
import com.thing.entity.XPropertyDefinition;
import com.thing.entity.XThingModel;
import com.thing.repos.ThingRepository;

@Service
public class StandardModelManager implements IThingModelManager {
	private ThingRepository repository;

	public ThingRepository repository() {
		if (repository == null) {
			repository = BasicConfiguration.getBean(ThingRepository.class);
		}

		return repository;
	}

	private IThingModel getParentThingModel(Class<?> clazz) {
		if (clazz == null)
			return null;

		Class<?> superCls = clazz.getSuperclass();
		while (true) {
			if (superCls == null)
				return null;

			if (IModelManaged.class.isAssignableFrom(superCls)) {
				IThingModel thingModel = this.getThingModel(superCls);
				if (thingModel != null) {
					return thingModel;
				}
			} else {
				return null;
			}

			superCls = superCls.getSuperclass();
		}
	}

	private IThingModel getParentThingModel(IThingModel model) {
		Class<?> entityClazz = model.getEntityClass();
		return getParentThingModel(entityClazz);
	}

	public IThingModel registerThingModel(Class<? extends IModelManaged> clazz) {
		if (clazz == null) {
			return null;
		}

		XDefinition definition = clazz.getAnnotation(XDefinition.class);
		if (definition == null) {
			throw new XException(clazz.getSimpleName() + "未定义XDefinition注解，无法注册!");
		}

		IThingModel thingModel = this.getThingModel(clazz.getName());
		if (thingModel == null) {
			IThingModel baseModel = this.getParentThingModel(clazz);
			thingModel = this.createThingModel(definition, clazz, baseModel);
		} else {
			thingModel.setName(definition.name());
			thingModel.setEntity(clazz.getName());
			thingModel.setModel(definition.config().getName());
			thingModel.setIcon(definition.icon());
			thingModel.setDisplay(definition.display());
			thingModel.setDescription(definition.description());
			thingModel.setPageUri(definition.pageUri());
			thingModel.setEn_US(definition.en_US());
			thingModel.setZh_CN(definition.zh_CN());
			IThingModel baseModel = getParentThingModel(thingModel);
			thingModel.setThingModel(baseModel);
			thingModel = PersistenceHelper.service().save(thingModel);
		}

		Map<String, IPropertyDefinition> propertyMap = new TreeMap<>();
		List<IPropertyDefinition> list = repository().getPropertyDefinition(thingModel);
		for (IPropertyDefinition property : list) {
			propertyMap.put(property.getName(), property);
		}
		Map<String, Field> fieldMap = thingModel.getNativeFields();
		for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
			String source = entry.getKey();
			Field field = entry.getValue();
			IPropertyDefinition property = propertyMap.get(field.getName());
			if (property == null) {
				property = XPropertyDefinition.newPropertyDefinition(thingModel, field);
				property.setSource(source);
				property = PersistenceHelper.service().save(property);
				propertyMap.put(property.getName(), property);
			}
		}

		return thingModel;
	}

	public IThingModel createThingModel(XDefinition definition, Class<?> entity, IThingModel baseModel) {
		String number = entity.getName();
		IThingModel thingModel = this.getThingModel(number);
		if (thingModel == null) {
			thingModel = new XThingModel();
			thingModel.setNumber(number);
			thingModel.setName(definition.name());
			thingModel.setEntity(entity.getName());
			thingModel.setModel(definition.config().getName());
			thingModel.setThingModel(baseModel);
			thingModel.setIcon(definition.icon());
			thingModel.setDisplay(definition.display());
			thingModel.setDescription(definition.description());
			thingModel.setPageUri(definition.pageUri());
			thingModel.setEn_US(definition.en_US());
			thingModel.setZh_CN(definition.zh_CN());
			PersistenceHelper.service().save(thingModel);
		} else {
			thingModel.setName(definition.name());
			thingModel.setEntity(entity.getName());
			thingModel.setModel(definition.config().getName());
			thingModel.setThingModel(baseModel);
			thingModel.setIcon(definition.icon());
			thingModel.setDisplay(definition.display());
			thingModel.setDescription(definition.description());
			thingModel.setPageUri(definition.pageUri());
			thingModel.setEn_US(definition.en_US());
			thingModel.setZh_CN(definition.zh_CN());
			PersistenceHelper.service().save(thingModel);
		}
		Map<String, IPropertyDefinition> propertyMap = new TreeMap<>();
		List<IPropertyDefinition> list = repository().getPropertyDefinition(thingModel);
		for (IPropertyDefinition property : list) {
			propertyMap.put(property.getName(), property);
		}
		Map<String, Field> fieldMap = thingModel.getNativeFields();
		for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
			String source = entry.getKey();
			Field field = entry.getValue();
			IPropertyDefinition property = propertyMap.get(field.getName());
			if (property == null) {
				property = XPropertyDefinition.newPropertyDefinition(thingModel, field);
				property.setSource(source);
				property = PersistenceHelper.service().save(property);
				propertyMap.put(property.getName(), property);
			}
		}

		return thingModel;
	}

	public IThingModel createThingModel(String number, String name, String icon, String description, String pageUri, String entity, String model, ILocalization localBean, IThingModel baseModel) {
		IThingModel thingModel = this.getThingModel(number);
		if (thingModel == null) {
			thingModel = new XThingModel();
			thingModel.setNumber(number);
			thingModel.setName(name);
			thingModel.setIcon(icon);
			thingModel.setEntity(entity);
			thingModel.setModel(model);
			thingModel.setDescription(description);
			thingModel.setPageUri(pageUri);
			thingModel.setThingModel(baseModel);
			thingModel.setDisplay(localBean.getDisplay());
			thingModel.setEn_US(localBean.getEn_US());
			thingModel.setZh_CN(localBean.getZh_CN());
			thingModel = PersistenceHelper.service().save(thingModel);
		} else {
			thingModel.setName(name);
			thingModel.setIcon(icon);
			thingModel.setEntity(entity);
			thingModel.setModel(model);
			thingModel.setDescription(description);
			thingModel.setPageUri(pageUri);
			thingModel.setThingModel(baseModel);
			thingModel.setDisplay(localBean.getDisplay());
			thingModel.setEn_US(localBean.getEn_US());
			thingModel.setZh_CN(localBean.getZh_CN());
			thingModel = PersistenceHelper.service().save(thingModel);
		}

		return thingModel;
	}

	public IThingModel getThingModel(String number) {
		return this.repository().getThingModel(number);
	}

	public IThingModel getThingModel(Class<?> clazz) {
		if (IModelManaged.class.isAssignableFrom(clazz)) {
			List<?> modelList = PersistenceHelper.service().query(XThingModel.class, new Object[][] { { "entity", clazz.getName() } });
			if (!modelList.isEmpty()) {
				return (XThingModel) modelList.get(0);
			}
		}

		return null;
	}

	@Override
	public List<IThingModel> getRootModel() {
		return this.repository().getRootModel();
	}

	@Override
	public List<IThingModel> getChildModel(IThingModel model) {
		return this.repository().getChildModel(model);
	}

	@Override
	public List<IPropertyDefinition> getPropertyDefinition(IThingModel model) {
		return this.repository().getPropertyDefinition(model);
	}

	@Override
	public IPropertyDefinition getPropertyDefinition(IThingModel model, String name) {
		return this.repository().getPropertyDefinition(model, name);
	}

	@Override
	public List<IServiceDefinition> getServiceDefinition(IThingModel model) {
		return this.repository().getServiceDefinition(model);
	}

	@Override
	public List<IPropertyLayout> getPropertyLayout(IThingModel model) {
		return this.repository().getPropertyLayout(model);
	}

	@Override
	public List<IPropertyLayout> getPropertyLayout(IThingModel model, LayoutType type) {
		return this.repository().getPropertyLayout(model, type);
	}
}
