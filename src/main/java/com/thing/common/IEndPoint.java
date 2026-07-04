package com.thing.common;

import com.flame.rpc.FlameMessage;
import com.flame.rpc.IReceiver;
import com.flame.rpc.ISender;

public interface IEndPoint extends IReceiver, ISender {
	String getIdentity();
	
	/**
	 * JSON-RPC的调用与返回的是通过id号进行匹配的，因此在同一个IConnection中，多个JSON-RPC请求
	 * 的id号必须保持唯一性，这个方法就是用来生成本IConnection中唯一JSON-RPC号的
	 * 
	 * @return
	 */
	String genRequestId();
	
	/**
	 * 当前IConnection通过FlameMessage调用客户端后，FlameMessage被阻塞在当前IConnection的messageCache
	 * 中，当客户端返回数据后，IRuntimeManager通过IConnection就会收到一个FlameResult，然后IRuntimeManager
	 * 根据FlameResult的id用getMessage()从IConnection中获取对应FlameMessage进行激活；
	 * 
	 * @param id
	 * @return
	 */
	FlameMessage getMessage(String id);
}
