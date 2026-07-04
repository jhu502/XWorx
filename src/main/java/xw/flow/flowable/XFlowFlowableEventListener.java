package xw.flow.flowable;

import org.flowable.common.engine.api.delegate.event.AbstractFlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XFlow定义的全局FlowableEventListener, 用来监听Flowable的FlowableEventType定义事件
 */
public class XFlowFlowableEventListener extends AbstractFlowableEventListener {
    protected static final Logger LOGGER = LoggerFactory.getLogger(FlowableEventListener.class);

    @Override
    public void onEvent(FlowableEvent event) {
        if (event instanceof FlowableEntityEvent) {
        	FlowableEntityEvent entityEvent = (FlowableEntityEvent) event;
        	FlowableEventType eventType = entityEvent.getType();
        	LOGGER.info(eventType.toString() + "-------X-------" + entityEvent.getEntity());
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
