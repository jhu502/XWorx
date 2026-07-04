package com.flame.thing;

import java.util.List;

import com.flame.annotations.XDefinition;
import com.flame.localize.ILocalization;

public interface IThingModelManager {
	IThingModel registerThingModel(Class<? extends IModelManaged> clazz);

	IThingModel createThingModel(XDefinition definition, Class<?> entity, IThingModel baseModel);

	IThingModel createThingModel(String number, String name, String icon, String description, String pageUri, String entity, String model, ILocalization localBean, IThingModel baseModel);

	IThingModel getThingModel(String number);

	IThingModel getThingModel(Class<?> clazz);

	List<IThingModel> getRootModel();

	List<IThingModel> getChildModel(IThingModel model);

	List<IPropertyDefinition> getPropertyDefinition(IThingModel model);

	IPropertyDefinition getPropertyDefinition(IThingModel model, String name);

	List<IServiceDefinition> getServiceDefinition(IThingModel model);

	List<IPropertyLayout> getPropertyLayout(IThingModel model);

	List<IPropertyLayout> getPropertyLayout(IThingModel model, LayoutType type);
}
