package xw.flow.bean;

import xw.flow.entity.XFlowGateway;

public class GatewayVO extends FlowNodeVO {

    public static GatewayVO newInstance(XFlowGateway gateway, boolean movable) {
        GatewayVO gatewayBean = new GatewayVO(gateway);
        gatewayBean.setMovable(movable);
        return gatewayBean;
    }

    public GatewayVO() {}

    public GatewayVO(XFlowGateway gateway) {
        super(gateway);
    }
}
