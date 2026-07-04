package com.thing.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.type.ServiceType;
import com.flame.annotations.XConfig;
import com.flame.annotations.XParam;
import com.flame.annotations.XService;
import com.flame.rpc.FlameMessage;
import com.flame.rpc.FlameRPCFactory;
import com.flame.rpc.FlameResult;
import com.flame.thing.IModelManaged;
import com.flame.thing.XBindType;
import com.flame.thing.XServiceType;
import com.flame.type.XBaseType;
import com.flame.util.XException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.thing.ThingPerformHelper;
import com.thing.ThingUtilities;
import com.thing.entity.XThingModel;
import com.thing.runtime.IScriptModel;
import com.thing.runtime.ScriptFunction;

/**
 * 在XWorx系统中，IThingEntity是真正运行时的Thing；
 * 调用Thing的服务时，ThingEntity自动根据绑定方式、Service类型来决定调用方式，例如：
 * a. @FTService类型是Local，ThingEntity自动调用代理ThingEntity的同名方法；
 * b. @FTService类型是Remote，ThingEntity会首先判断是否有远程VirtualThing与之绑定；若无：提示无绑定的错误信息；若有：通过Json-RPC调用远程绑定的VirtualThing；
 * 
 * 绑定类型是BindType.OffSite(这种情况只会在分布式的环境下出现)，说明当前ThingEntity已经绑定到其他服务器，ThingEntity自动通过RPC在服务器之间进行远程调用；
 * 
 * @author hujin
 *
 */

public class AbstractThingModel<S extends IModelManaged> implements IBindable, IThingManaged<S> {
	private static Logger logger = LoggerFactory.getLogger(AbstractThingModel.class);
	protected static final String MissMethod = "MissMethod";

	protected XBindType bindType = XBindType.None;
	@JsonIgnore
	private transient S thingEntity;
	@JsonIgnore
	private transient IScriptModel scriptModel;
	@JsonIgnore
	private transient Value Script_SERVICE;
	/** ThingModel页面，通过Javascript脚本定义的Service方法 */
	@JsonIgnore
	private transient Map<String, ServiceMethod> Native_SERVICE = new HashMap<String, ServiceMethod>();
	/** 通过FTService注解定义的Java Service方法 */
	private Map<String, Field> configItems = new HashMap<String, Field>();
	/**
	 * IConnection主动发起的与IThingEntity的绑定，IConnection可以绑定多个IThingEntity，
	 * 但是IThingEntity不允许被多个IConnection绑定，不允许被一个IConnection绑定多次；
	 */
	@JsonIgnore
	private transient IEndPoint endpoint;

	private Map<String, Object> properties = new HashMap<String, Object>();

	public AbstractThingModel() {
	}

	class ServiceMethod {
		private ServiceType type;
		private String name;
		private Method service;
		private XParam[] params;
		private Object object;
	}

	public AbstractThingModel(S target) {
		this.thingEntity = target;
	}

	@Override
	public void setThingEntity(S target) {
		this.thingEntity = (S) target;
	}

	@JsonIgnore
	public S getThingEntity() {
		return this.thingEntity;
	}

	public long getId() {
		return this.getThingEntity().getXid();
	}

	public String getThingIdentity() {
		return this.getThingEntity().getThingIdentity();
	}

	public void startThing() {
	}

	public void stopThing() {
	}

	protected void initializeThing() throws Exception {
	}

	protected void cleanupThing() throws Exception {
	}

	public void DisableThing() throws Exception {
	}

	public void EnableThing() throws Exception {
	}

	/**
	 * 将Java定义的原生Service、Javascript定义脚本Service加载进Thing Entity中
	 */
	public void inflateServiceEnvironment() {
		XThingModel thingModel = (XThingModel) this.getThingEntity().getThingModel();
		this.scriptModel = ThingPerformHelper.service().getScriptModelFunction(thingModel);
		this.Script_SERVICE = this.scriptModel.getNewInstance(this);

		/**
		 * 加载 Service
		 */
		Class<?> configCls = this.thingEntity.getClass();
		for (Method method : configCls.getMethods()) {
			XService ftservice = method.getAnnotation(XService.class);
			if (ftservice != null) {
				ServiceMethod serviceObj = new ServiceMethod();
				serviceObj.name = method.getName();
				serviceObj.type = ftservice.serviceType();
				serviceObj.object = this.thingEntity;
				serviceObj.service = method;
				serviceObj.params = ftservice.params();

				this.Native_SERVICE.put(serviceObj.name, serviceObj);
			}
		}

		for (Field field : ThingUtilities.getDeclaredFields(configCls)) {
			XConfig config = field.getAnnotation(XConfig.class);
			if (config != null) {
				this.configItems.put(config.name(), field);
				field.setAccessible(true);
			}
		}

		Class<?> thingCls = this.getClass();
		for (Method method : thingCls.getMethods()) {
			XService ftservice = method.getAnnotation(XService.class);
			if (ftservice != null) {
				ServiceMethod serviceObj = new ServiceMethod();
				serviceObj.name = method.getName();
				serviceObj.type = ftservice.serviceType();
				serviceObj.object = this;
				serviceObj.service = method;
				serviceObj.params = ftservice.params();

				if (this.Native_SERVICE.containsKey(serviceObj.name)) {
					throw new XException("Service:" + serviceObj.name + " conflict!");
				}
				this.Native_SERVICE.put(serviceObj.name, serviceObj);
			}
		}
	}

	@Override
	public XBindType getBindType() {
		return this.bindType;
	}

	@Override
	public void setBindType(XBindType btype) {
		this.bindType = btype;
	}

	@Override
	public IEndPoint getEndPoint() {
		return this.endpoint;
	}

	@Override
	public boolean bind(IEndPoint endpoint) {
		this.startThing();
		this.endpoint = endpoint;
		this.setBindType(XBindType.OnSite);

		return true;
	}

	@Override
	public boolean unbind(IEndPoint endpoint) {
		if (endpoint == null)
			return false;

		if (this.endpoint == null) {
			this.setBindType(XBindType.None);
			return true;
		}

		if (endpoint.getIdentity().equals(this.endpoint.getIdentity())) {
			this.setBindType(XBindType.None);
			this.endpoint = null;

			return true;
		}

		return false;
	}

	@Override
	public boolean isBinding() {
		return this.endpoint != null;
	}

	private void checkArgsConsistency(Object[] argument, XParam[] params) {
		if (argument != null && params == null) {
			throw new XException("Parameter values do not match the definition!");
		}
		if ((argument == null && params != null)) {
			throw new XException("Parameter values do not match the definition!");
		}
		if (argument != null && params != null && argument.length != params.length) {
			throw new XException("Parameter values do not match the definition!");
		}
	}

	@Override
	public Object invokeService(String methodName, Object... arguments) throws Exception {
		logger.debug("Invoke method: {} with thing: {}", methodName, this.thingEntity.getThingIdentity());

		/** 优先从ScriptService中查询同名的方法，然后调用 */
		if (this.Script_SERVICE != null && this.Script_SERVICE.hasMember(methodName)) {
			ScriptFunction funcModel = this.scriptModel.getScriptFunction(methodName);
			if (funcModel != null) {
				XBaseType[] types = funcModel.getArguments();
				Object[] _params = transferParamType(types, arguments);
				return this.Script_SERVICE.invokeMember(methodName, _params);
			}
		}

		/** 如果在SrciptService无同名的方法，则从NativeService中调用同名方法 */
		ServiceMethod serviceMethod = this.Native_SERVICE.get(methodName);
		if (serviceMethod != null) {
			if (XServiceType.Remote.equals(serviceMethod.type)) {
				if (XBindType.OnSite.equals(this.getBindType())) {
					this.checkArgsConsistency(arguments, serviceMethod.params);
					FlameMessage message = FlameRPCFactory.genFlameMessage(this.endpoint.genRequestId(), this.thingEntity.getThingIdentity(), methodName, serviceMethod.params, arguments);

					if (this.endpoint instanceof AbstractEndPoint) {
						AbstractEndPoint endpoint = (AbstractEndPoint) this.endpoint;
						FlameResult result = endpoint.synInvoke(message);
						return result.getResult();
					}
				} else if (XBindType.OffSite.equals(this.getBindType())) {
					return null;
				} else {
					throw new XException(this.getThingIdentity() + "没有客户端与其绑定，调用Remote方法无效!");
				}
			} else {
				Object[] _params = transferParamType(serviceMethod.service, arguments);
				return serviceMethod.service.invoke(serviceMethod.object, _params);
			}
		}
		throw new XException("There is no such Service Definiton:" + methodName);
	}

	public Object invokeMember(String methodName, Object... arguments) {
		try {
			return this.invokeService(methodName, arguments);
		} catch (XException e) {
			throw e;
		} catch (Exception e) {
			throw new XException(e);
		}
	}

	@Override
	public boolean hasProperty(String propName) {
		return this.scriptModel.hasProperty(propName);
	}

	@Override
	public Object readProperty(String propName) throws UnknownIdentifierException {
		Object value = this.properties.get(propName);
		if (value == null) {
			value = this.scriptModel.getDefaultValue(propName);
		}
		return value;
	}

	@Override
	public void writeProperty(String propName, Object value) throws UnknownIdentifierException {
		if (this.properties.containsKey(propName)) {
			this.properties.put(propName, value);
		} else {
			if (this.hasProperty(propName)) {
				this.properties.put(propName, value);
			} else {
				throw UnknownIdentifierException.create(propName);
			}
		}
	}

	@XService(
		serviceType = ServiceType.Local,
		name = "getConfiguration",
		resultType = XBaseType.OBJECT,
		params = { @XParam(
			name = "configName",
			type = XBaseType.STRING) })
	public Object getConfiguration(String configName) {
		Field field = this.configItems.get(configName);
		if (field == null)
			return null;

		S entity = this.getThingEntity();
		try {
			return field.get(entity);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new XException(e);
		}
	}

	/**
	 * 最核心的方法之一，用来进行jsonrpc方法参数的转换
	 * 
	 * @param object
	 * @param arguments
	 * @return
	 * @throws Exception
	 */
	private Object[] transferParamType(Object object, Object[] arguments) throws Exception {
		if (object instanceof Method) { // 在调用Java方法时进行参数类型转换
			Method method = (Method) object;
			Object[] result = new Object[method.getParameterCount()];
			int i = 0;
			for (Parameter param : method.getParameters()) {
				Object value = arguments[i];
				if (value.getClass().equals(param.getType())) {
					result[i] = value;
				} else if (String.class.equals(param.getType())) {
					result[i] = value.toString();
				} else if (Double.class.equals(param.getType()) || double.class.equals(param.getType())) {
					result[i] = Double.parseDouble(value.toString());
				} else if (Integer.class.equals(param.getType()) || int.class.equals(param.getType())) {
					result[i] = Integer.parseInt(value.toString());
				} else if (Float.class.equals(param.getType()) || float.class.equals(param.getType())) {
					result[i] = Float.parseFloat(value.toString());
				} else if (Long.class.equals(param.getType()) || long.class.equals(param.getType())) {
					result[i] = Long.parseLong(value.toString());
				} else if (Boolean.class.equals(param.getType()) || boolean.class.equals(param.getType())) {
					result[i] = Boolean.parseBoolean(value.toString());
				}
				i = i + 1;
			}

			return result;
		} else if (object instanceof XBaseType[]) { // 主要在调用Javascript Service时进行参数类型转换
			XBaseType[] types = (XBaseType[]) object;
			Object[] result = new Object[types.length];
			for (int i = 0; i < types.length; i++) {
				XBaseType type = types[i];
				Object value = arguments[i];
				result[i] = type.getPrimitive(value).getValue();
			}

			return result;
		} else {
			return arguments;
		}
	}
}
