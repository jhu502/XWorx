package com.thing.runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.flame.rpc.FlameMessage;
import com.flame.rpc.FlameRPCFactory;
import com.flame.rpc.FlameResult;
import com.flame.thing.IModelManaged;
import com.flame.thing.IThingModel;
import com.flame.thing.XBindType;
import com.flame.util.XException;
import com.thing.ThingEntityHelper;
import com.thing.ThingUtilities;
import com.thing.common.AbstractEndPoint;
import com.thing.common.AbstractThingModel;
import com.thing.common.IBindable;
import com.thing.common.IConnectable;
import com.thing.common.IEndPoint;
import com.thing.common.IThingManaged;
import com.thing.common.PlatformThingEntity;

@Service
public class ThingDispatchManager implements IDispatchManager, IThingManager {
	private static final Logger logger = LoggerFactory.getLogger(ThingDispatchManager.class);

	private Map<String, IThingManaged<IModelManaged>> SYSTEM_RUNNING_MAP = new ConcurrentHashMap<String, IThingManaged<IModelManaged>>();
	/**
	 * 正在运行的Thing存放在THING_RUNNING_MAP中，主要包括下面几种： 1. 与客户端有绑定的Thing; 2.
	 * 与数据库建立了连接的Thing，其实也是某种程度的绑定;
	 */
	private Map<String, IThingManaged<IModelManaged>> THING_RUNNING_MAP = new ConcurrentHashMap<String, IThingManaged<IModelManaged>>();
	/**
	 * 记录处于连接中EndPoint，Key值是EndPoint的Identity
	 */
	private Map<String, IEndPoint> ENDPOINT_REGISTER_MAP = new WeakHashMap<String, IEndPoint>();
	/**
	 * 记录EndPoint绑定的ThingEntity，Key值是EndPoint的Identity
	 */
	private Map<String, Map<String, IBindable>> ENDPOINT_BINDABLE_MAP = new WeakHashMap<String, Map<String, IBindable>>();

	public ThingDispatchManager() {
		this.loadFlamethrowerSystem();
	}

	private void ENDPOINT_BINDABLE_MAP(IEndPoint point, IBindable bindable) {
		Map<String, IBindable> map = this.ENDPOINT_BINDABLE_MAP.get(point.getIdentity());
		if (map == null) {
			map = new HashMap<String, IBindable>();
			this.ENDPOINT_BINDABLE_MAP.put(point.getIdentity(), map);
		}
		if (!map.containsKey(bindable.getThingIdentity())) {
			map.put(bindable.getThingIdentity(), bindable);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void loadFlamethrowerSystem() {
		PlatformThingEntity ftplatform = new PlatformThingEntity();
		ftplatform.setBindType(XBindType.System);
		this.SYSTEM_RUNNING_MAP.put(ThingEntityHelper.PLATFORM_THING, (IThingManaged) ftplatform);
	}

	@Override
	public void requestFlameMessage(IEndPoint endpoint, FlameMessage message) {
		logger.trace("requestFlameMessage:{}", FlameRPCFactory.toJsonString(message));

		FlameResult flameResult = null;
		try {
			flameResult = dispatchService(message, endpoint);
		} finally {
			if (endpoint instanceof AbstractEndPoint) {
				endpoint.sendResponse(flameResult);
			}
		}
	}

	@Override
	public void responseFlameResult(IEndPoint connect, FlameResult result) {
		logger.trace("responseFlameResult:{}", FlameRPCFactory.toJsonString(result));

		FlameMessage fMessage = connect.getMessage(result.getId());
		if (fMessage != null) {
			fMessage.setResult(result);
			synchronized (fMessage) {
				fMessage.notifyAll();
			}
		}
	}

	@Override
	public FlameResult dispatchService(FlameMessage message, IEndPoint endpoint) {
		IThingManaged<?> thingEntity = this.getInflatedThingEntity(message.getThing());
		try {
			if (thingEntity instanceof PlatformThingEntity) {
				PlatformThingEntity platform = (PlatformThingEntity) thingEntity;
				if ("binding".equals(message.getMethod())) {
					String identity = (String) message.getParams().get("identity");
					platform.binding(identity, endpoint);
					return FlameRPCFactory.genFlameResult(message, "Identity:" + identity + " bind successfully.");
				} else {
					Object result = thingEntity.invokeService(message.getMethod(), message.getParams().values().toArray());
					return FlameRPCFactory.genFlameResult(message, result);
				}
			} else {
				Object result = thingEntity.invokeService(message.getMethod(), message.getParams().values().toArray()); // PrimitiveValue
				return FlameRPCFactory.genFlameResult(message, result);
			}
		} catch (InvocationTargetException e) {
			return FlameRPCFactory.genFlameResult(message, e);
		} catch (Exception e) {
			return FlameRPCFactory.genFlameResult(message, e);
		}
	}

	public IThingManaged<IModelManaged> getThing(String identity) {
		return this.getInflatedThingEntity(identity);
	}

	private IThingManaged<IModelManaged> getRunningThing(String thingIdentity) {
		IThingManaged<IModelManaged> thingEntity = this.SYSTEM_RUNNING_MAP.get(thingIdentity);
		thingEntity = thingEntity == null ? this.THING_RUNNING_MAP.get(thingIdentity) : thingEntity;

		return thingEntity;
	}

	@Override
	@SuppressWarnings("unchecked")
	public IThingManaged<IModelManaged> getInflatedThingEntity(String thingIdentity) {
		IThingManaged<IModelManaged> thingEntity = getRunningThing(thingIdentity);

		if (thingEntity == null) {
			IModelManaged entity = ThingEntityHelper.service().findConfigEntity(thingIdentity);
			try {
				IThingModel thingModel = (IThingModel) entity.getThingModel();
				Constructor<?> constuct = thingModel.getModelClass().getDeclaredConstructor(new Class[] { entity.getClass() });
				constuct.setAccessible(true);
				thingEntity = (IThingManaged<IModelManaged>) constuct.newInstance(entity);
				if (thingEntity instanceof AbstractThingModel) {
					((AbstractThingModel<IModelManaged>) thingEntity).inflateServiceEnvironment();
				}
			} catch (Exception e) {
				throw new XException(e);
			}
		}

		return thingEntity;
	}

	public IThingManaged<IModelManaged> binding(String thingIdentity, IEndPoint endpoint) {
		if (!ThingUtilities.checkThingIdentity(thingIdentity)) {
			throw new XException("Identity format error. e.g.:(XUser:Guest)");
		}

		IThingManaged<IModelManaged> thingEntity = getRunningThing(thingIdentity);

		if (thingEntity != null) {
			IBindable bindable = (IBindable) thingEntity;
			if (XBindType.OnSite == bindable.getBindType()) {
				IEndPoint _endpoint = bindable.getEndPoint();
				if (_endpoint == null) {
					((IBindable) thingEntity).bind(endpoint);
				} else {
					throw new XException("Other client is binding with thing " + thingIdentity + "!");
				}
			} else {
				bindable.bind(endpoint);
			}
		} else {
			thingEntity = this.getInflatedThingEntity(thingIdentity);
			if (thingEntity == null) {
				throw new XException("Thing " + thingIdentity + " don't exist!");
			} else {
				((IBindable) thingEntity).bind(endpoint);
				this.THING_RUNNING_MAP.put(thingIdentity, thingEntity);
			}
		}

		this.ENDPOINT_BINDABLE_MAP(endpoint, (IBindable) thingEntity);

		return thingEntity;
	}

	@Override
	public String regEndPoint(IEndPoint endpoint) {
		this.ENDPOINT_REGISTER_MAP.put(endpoint.getIdentity(), endpoint);
		return endpoint.getIdentity();
	}

	public void disEndPoint(IEndPoint endpoint) {
		Map<String, IBindable> bindings = this.ENDPOINT_BINDABLE_MAP.get(endpoint.getIdentity());
		if (bindings == null) {
			return;
		}

		try {
			synchronized (endpoint) {
				for (IBindable bindable : bindings.values()) {
					IEndPoint _endpoint = bindable.getEndPoint();
					if (_endpoint == null)
						continue;

					if (bindable.unbind(endpoint)) {

						if (bindable instanceof IConnectable) {
							IConnectable<?> connable = (IConnectable<?>) bindable;
							if (!connable.isConnected()) {
								this.THING_RUNNING_MAP.remove(((IThingManaged<?>) bindable).getThingIdentity());
							}
						} else {
							this.THING_RUNNING_MAP.remove(((IThingManaged<?>) bindable).getThingIdentity());
						}
					}
				}
			}
		} finally {
			this.ENDPOINT_REGISTER_MAP.remove(endpoint.getIdentity());
		}
	}

	@Override
	public IThingManaged<?> startThing(String identity) {
		IThingManaged<IModelManaged> thingEntity = this.getInflatedThingEntity(identity);

		thingEntity.startThing();

		if (!this.THING_RUNNING_MAP.containsKey(identity)) {
			this.THING_RUNNING_MAP.put(identity, thingEntity);
		}

		return thingEntity;
	}

	@Override
	public IThingManaged<?> stopThing(String identity) {
		IThingManaged<IModelManaged> thing = this.getInflatedThingEntity(identity);

		thing.stopThing();

		boolean bool = false;
		if (!bool && thing instanceof IConnectable) {
			bool = ((IConnectable<?>) thing).isConnected();
		}
		if (!bool && thing instanceof IBindable) {
			bool = ((IBindable) thing).isBinding();
		}

		if (!bool) {
			this.THING_RUNNING_MAP.remove(identity);
		}

		return thing;
	}
}
