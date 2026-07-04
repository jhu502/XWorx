package xw.flow.bean;

import xw.flow.entity.XFlowThing;

public class ThingNodeVO extends FlowNodeVO {

    public static ThingNodeVO newInstance(XFlowThing thing, boolean movable) {
        ThingNodeVO thingBean = new ThingNodeVO(thing);
        thingBean.setMovable(movable);
        return thingBean;
    }

    public ThingNodeVO() {}

    public ThingNodeVO(XFlowThing thing) {
        super(thing);
    }
}
