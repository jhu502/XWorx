package com.thing.common;

import com.flame.auths.IUser;
import com.flame.rpc.FlameMessage;
import com.flame.rpc.FlameRPC;
import com.flame.rpc.FlameRPCFactory;
import com.flame.rpc.FlameResult;
import com.flame.util.XException;

import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractEndPoint implements IEndPoint {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractEndPoint.class);
    private AtomicLong rpcId = new AtomicLong(0);
    private String identity = UUID.randomUUID().toString();
    protected IUser currentUser;
    protected Session session;
    protected RemoteEndpoint.Async async;
    private Map<String, FlameMessage> messageCache = new ConcurrentHashMap<>();

    public FlameMessage getMessage(String id) {
        return this.messageCache.get(id);
    }

    public void addMessage(FlameMessage message) {
        this.messageCache.put(message.getId(), message);
    }

    public void removeMessage(String id) {
        this.messageCache.remove(id);
    }

    public String getIdentity() {
        return this.identity;
    }

    public String genRequestId() {
        return Long.toString(rpcId.incrementAndGet());
    }
    
    public FlameRPC receiveRequest(String rawData) {
        Object rpcObject = FlameRPCFactory.decodeRPC(rawData);
        if (rpcObject instanceof FlameRPC) {
            FlameRPC flameRPC = (FlameRPC) rpcObject;
            return flameRPC;
        } else {
            logger.error("Received non-RPC message: {}", rawData);
            return null;
        }
    }

    public void sendResponse(FlameResult result) {
        try {
            if (this.async == null) {
                this.async = this.session.getAsyncRemote();
            }
            logger.trace("User:{} Send:{}", this.currentUser.getName(), result.toJsonString());
            String result_encode = FlameRPCFactory.encodeRPC(result);
            this.async.sendText(result_encode);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void asynInvoke(FlameMessage message) {
        try {
            if (this.async == null) {
                this.async = this.session.getAsyncRemote();
            }
            this.async.sendText(FlameRPCFactory.encodeRPC(message));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public FlameResult synInvoke(FlameMessage message) {
        try {
            this.addMessage(message);
            /**
             * RemoteEndpoint.Async在高并发时，多个线程同时使用同一session发送，会出现异常：
             * 	java.lang.IllegalStateException: The remote endpoint was in state [TEXT_FULL_WRITING] which is an invalid state for called method
             */
            synchronized (this) {
                synchronized (message) {
                    this.asynInvoke(message);

                    /** 使用Object.wait()等待返回结果：FlameResult */
                    message.wait(3000);
                    if (message.getResult() == null) {
                        logger.error("Request:{} invoke time out.", message.getId());
                        FlameResult result = new FlameResult();
                        result.setId(message.getId());
                        result.setException(new XException("Invoke time out."));
                        message.setResult(result);
                    }
                }
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } finally {
            this.removeMessage(message.getId());
        }

        return message.getResult();
    }

    @Override
    public void shutdown() {
    }
}
