package com.thing.runtime;

import com.flame.rpc.FlameMessage;
import com.flame.rpc.FlameResult;
import com.flame.thing.IModelManaged;
import com.thing.common.IEndPoint;
import com.thing.common.IThingManaged;

public interface IDispatchManager {
	IThingManaged<IModelManaged> binding(String identity, IEndPoint endpoint);

	String regEndPoint(IEndPoint endpoint);

	void disEndPoint(IEndPoint endpoint);

	/**
	 * 系统中，ThingEntity是能够直接被远程调用的，这个函数根据传入的ThingModel的Number与IConfigEntity的Number组合
	 * 成唯一的Thing识别号(例如：“XUser:Guest”)，然后根据ThingModel的ThingEntity信息生成对应的ThingEntity对象
	 * 
	 * @param thingIdentity
	 * @return
	 */
	IThingManaged<IModelManaged> getInflatedThingEntity(String thingIdentity);

	/**
	 * 来自于IConnection的JSON-RPC的请求被转换成FlameMessage对象后，委托给requestHandle()
	 * 去执行，执行结果作为FlameResult对象赋值给FlameMessage，然后调用IConnection的result()返
	 * 回给请求方向
	 * 
	 * @param endpoint
	 * @param message
	 */
	void requestFlameMessage(IEndPoint endpoint, FlameMessage message);

	/**
	 * 来自于IConnection的JSON-RPC的返回被转换成FlameResult对象后，委托给responseHandle()处理，
	 * reponseHandle()根据FlameResult的id获取对应的FlameMessage，然后调用FlameMessage的
	 * NotifyAll()方法激活阻塞。
	 * 
	 * @param endpoint
	 * @param result
	 */
	void responseFlameResult(IEndPoint endpoint, FlameResult result);
}
