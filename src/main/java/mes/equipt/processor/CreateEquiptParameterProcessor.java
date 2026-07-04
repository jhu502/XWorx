package mes.equipt.processor;

import mes.equipt.XEquiptInstance;
import mes.equipt.XEquiptParameter;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.type.XBaseType;

public class CreateEquiptParameterProcessor extends DefaultFormProcessor {

    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        String number = commandBean.getTextParameter("number");
        String name = commandBean.getTextParameter("name");
        String description = commandBean.getTextParameter("description");
		String baseType = commandBean.getTextParameter("baseType");

        XObject primary = commandBean.getPrimaryObj();
        if (primary instanceof XEquiptInstance) {
            XEquiptInstance equipInstance = (XEquiptInstance) primary;
            XEquiptParameter equiptParameter = XEquiptParameter.newXEquiptParameter(equipInstance, number, name, description);
            equiptParameter.setBaseType(XBaseType.valueOf(baseType));
            equiptParameter = PersistenceHelper.service().save(equiptParameter);

            formResult.setData(equiptParameter);
        }

        return formResult;
    }
}
