package xw.flow.processor;

import xw.flow.XFlowDefinitionHelper;
import xw.flow.entity.XFlowDefinition;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;

import java.util.List;

public class DeployFlowDefinitionProcessor extends DefaultFormProcessor {
    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        List<Object> rowObjs = commandBean.getRowObjects();
        for (Object rowObj : rowObjs) {
            if (rowObj instanceof XFlowDefinition) {
                XFlowDefinition definition = (XFlowDefinition) rowObj;
                XFlowDefinitionHelper.definition().deployFlowDefinition(definition);
            }
        }

        return formResult;
    }
}
