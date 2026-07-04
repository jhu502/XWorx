package xw.flow.processor;

import xw.flow.constants.FlowConstant;
import xw.flow.entity.XWorkInstance;
import xw.flow.XFlowExecutionHelper;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.XObject;

public class CompleteFlowNodeProcessor extends DefaultFormProcessor {
    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        XObject primary = commandBean.getPrimaryObj();
        String nodeId = commandBean.getTextParameter(FlowConstant.NODEID);
        if (primary instanceof XWorkInstance) {
            XWorkInstance flowInstance = (XWorkInstance) primary;
            XFlowExecutionHelper.execution().completeActivity(flowInstance, nodeId);
        }

        return formResult;
    }
}
