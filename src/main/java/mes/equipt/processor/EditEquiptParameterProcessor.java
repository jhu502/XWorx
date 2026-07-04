package mes.equipt.processor;

import mes.equipt.XEquiptParameter;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;

import java.util.List;

public class EditEquiptParameterProcessor extends DefaultFormProcessor {

    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        String name = commandBean.getTextParameter("name");
        String description = commandBean.getTextParameter("description");
        String value = commandBean.getTextParameter("value");

        List<Object> rowObjs = commandBean.getRowObjects();
        for (Object rowObject : rowObjs) {
            if (rowObject instanceof XEquiptParameter) {
                XEquiptParameter equiptParameter = (XEquiptParameter) rowObject;
                equiptParameter.setName(name);
                equiptParameter.setDescription(description);
                equiptParameter.setValue(value);
                equiptParameter = PersistenceHelper.service().save(equiptParameter);

                formResult.setData(equiptParameter);
            }
        }

        return formResult;
    }
}
