package xw.flow.processor;

import xw.flow.entity.XWorkInstance;
import xw.flow.XFlowExecutionHelper;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;

import java.util.List;

public class DeleteXWorkInstanceProcessor extends DefaultFormProcessor {
    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        List<Object> rowObjs = commandBean.getRowObjects();
        for (Object rowObj : rowObjs) {
            if (rowObj instanceof XWorkInstance) {
                XWorkInstance flowInstance = (XWorkInstance) rowObj;
                XFlowExecutionHelper.execution().removeProcessInstance(flowInstance);
            }
        }

        return formResult;
    }
}
