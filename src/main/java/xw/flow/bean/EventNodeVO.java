package xw.flow.bean;

import xw.flow.entity.XFlowEvent;

public class EventNodeVO extends FlowNodeVO {

    public static EventNodeVO newInstance(XFlowEvent event, boolean movable) {
        EventNodeVO eventBean = new EventNodeVO(event);
        eventBean.setMovable(movable);
        return eventBean;
    }

    public EventNodeVO() {}

    public EventNodeVO(XFlowEvent event) {
        super(event);
    }

}
