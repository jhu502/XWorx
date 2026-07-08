package com.flame.config.system;

import com.flame.auths.IUser;
import com.flame.auths.SessionHelper;
import com.flame.config.basic.XHttpSessionConfigurator;
import com.flame.rpc.FlameMessage;
import com.flame.rpc.FlameRPC;
import com.flame.rpc.FlameResult;
import com.thing.ThingEntityHelper;
import com.thing.common.AbstractEndPoint;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@ServerEndpoint(value = "/WebSocket/Flame", configurator = XHttpSessionConfigurator.class)
public class FlameWebsocketEndPoint extends AbstractEndPoint {
    protected static final Logger logger = LoggerFactory.getLogger(FlameWebsocketEndPoint.class);
    private IUser currentUser;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;

        try {
            Principal principal = this.session.getUserPrincipal();
            this.currentUser = SessionHelper.getUserByName(principal.getName());

            // 设置 WebSocket 消息大小限制，避免长消息被截断
            session.setMaxTextMessageBufferSize(10 * 1024 * 1024);
            session.setMaxBinaryMessageBufferSize(10 * 1024 * 1024);

            logger.info("User: {} open websocket connect!", currentUser.getName());
            ThingEntityHelper.dispatch().regEndPoint(this);
            FlameResult result = new FlameResult();
            result.setResult("Login Successfully.");
            this.sendResponse(result);
        } finally {
        }
    }

    @OnMessage
    public void onMessage(String rawMessage) {
        try {
            Object rpcObject = this.receiveRequest(rawMessage);
            if (rpcObject instanceof FlameRPC) {
                logger.debug(Thread.currentThread().getName() + ":User:{} Receive:{}", SessionHelper.getCurrentUser().getName(), ((FlameRPC) rpcObject).toJsonString());

                if (rpcObject instanceof FlameMessage) {
                    FlameMessage flameMessage = (FlameMessage) rpcObject;
                    ThingEntityHelper.requestFlameMessage(this, flameMessage);
                } else if (rpcObject instanceof FlameResult) {
                    FlameResult flameResult = (FlameResult) rpcObject;
                    ThingEntityHelper.responseFlameResult(this, flameResult);
                } else {
                    this.onMessage((FlameRPC) rpcObject);
                }
            } else {
                logger.warn("Received non-RPC message: {}", rawMessage);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected void onMessage(FlameRPC flameRpc) {

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        try {
            logger.info("User:{} close websocket connect!", this.session.getUserPrincipal().getName());
            ThingEntityHelper.dispatch().disEndPoint(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Throwable error) {
    }
}
