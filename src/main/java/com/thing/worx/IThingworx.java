package com.thing.worx;

import com.flame.type.IPrimitiveType;
import org.json.JSONObject;

import com.flame.type.XInfoTable;
import com.flame.type.XRelationTypes;
import com.flame.type.XValueCollection;
import com.flame.util.XException;

/**
 * Thingworx&XWorx集成接口，Thingworx的XFlameSubsystem子系统实现这个IThingworx接口；
 * 在Thingworx启动加载XFlameSubsystem时，会将XFlameSubsystem注册进XWorx的ApplicationContext上下文，XWorx平台就可以通过IThingworx接口去调用XFlameSubsystem及进一步去调用Thingworx；
 * 
 * @author hujin
 *
 */
public interface IThingworx {
	Object getEntity(String name);

	Object getBaseManager(XRelationTypes types) throws XException;

	Object getPropertyValue(String thingName, String propertyName) throws XException;

	/**
	 * XWorx通过XFlameSubsystem去设置Thing的属性值
	 *
	 * @param thingName String Thing名称
	 * @param propertyName String Property名称
	 * @param value	XPrimitiveType 设置的属性值
	 * @throws XException
	 */
	void setPropertyValue(String thingName, String propertyName, IPrimitiveType<?> value) throws XException;

	void addPropertyDefinition(String thingName, String name, String description, String type, String category, String dataShape, Boolean readOnly, Boolean persistent, Boolean logged, Boolean indexed,
			String dataChangeType, Double dataChangeThreshold, Boolean remote, String remotePropertyName, Integer timeout, String pushType, Double pushThreshold, String defaultValue,
			JSONObject remoteBindingAspects) throws XException;

	public void delPropertyDefinition(String thingName, String name) throws XException;

	XInfoTable invokeThingService(String thingName, String service, XValueCollection arguments) throws XException;
}
