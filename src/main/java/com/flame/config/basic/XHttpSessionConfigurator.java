package com.flame.config.basic;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import jakarta.websocket.server.ServerEndpointConfig.Configurator;

/**
 * 	被ServerEndpoint使用去从当前上下文获取HttpSession，避免页面连接WebSocket需要重复登录的问题
 * @author hujin
 *
 */
public class XHttpSessionConfigurator extends Configurator {

	@Override
	public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
		HttpSession httpSession = (HttpSession) request.getHttpSession();
		sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
	}
}
