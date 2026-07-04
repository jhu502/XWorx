package xw.flow.processor;

import java.util.Map;
import java.util.UUID;

import xw.flow.bean.EventNodeVO;
import xw.flow.bean.FlowDataBean;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import xw.flow.constants.FlowConstant;

public class InitProcessDefinitionProcessor extends DefaultFormProcessor {
    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);

        FlowDataBean flowData = new FlowDataBean();
        EventNodeVO startNode = new EventNodeVO();
        startNode.setId(UUID.randomUUID().toString());
        startNode.setShape(FlowConstant.X_NODE);
        startNode.setXType(FlowConstant.START_EVENT);
        startNode.setLabel("开始");
        startNode.addAttr(FlowConstant.IMAGE, Map.of("xlink:href", "images/flow/start.png"));
        startNode.addAttr(FlowConstant.LABEL, Map.of(FlowConstant.TEXT, "开始"));
        startNode.setMovable(true);
        startNode.setX(40);
        startNode.setY(300);
        flowData.getNodes().add(startNode);

        EventNodeVO endNode = new EventNodeVO();
        endNode.setId(UUID.randomUUID().toString());
        endNode.setShape(FlowConstant.X_NODE);
        endNode.setXType(FlowConstant.END_EVENT);
        endNode.setLabel("结束");
        endNode.addAttr(FlowConstant.IMAGE, Map.of("xlink:href", "images/flow/end.png"));
        endNode.addAttr(FlowConstant.LABEL, Map.of(FlowConstant.TEXT, "结束"));
        endNode.setMovable(true);
        endNode.setX(500);
        endNode.setY(300);
        flowData.getNodes().add(endNode);

        formResult.setData(flowData);
        return formResult;
    }
}
