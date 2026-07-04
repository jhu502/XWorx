package xw.context.processor;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;

public class CreateOrganizationProcessor extends DefaultFormProcessor {
    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        String number = commandBean.getTextParameter("number");
        String name = commandBean.getTextParameter("name");
        String description = commandBean.getTextParameter("description");


        return formResult;
    }
}
