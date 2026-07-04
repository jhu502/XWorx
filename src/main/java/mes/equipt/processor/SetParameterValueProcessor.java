package mes.equipt.processor;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.type.IPrimitiveType;
import mes.equipt.XEquiptInstance;
import mes.equipt.XEquiptParameter;
import com.thing.worx.ThingworxHelper;

public class SetParameterValueProcessor extends DefaultFormProcessor {

    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        String name = commandBean.getTextParameter("name");
        String description = commandBean.getTextParameter("description");
        String value = commandBean.getTextParameter("value");

        XObject primary = commandBean.getPrimaryObj();
        if (primary instanceof XEquiptInstance) {
            Object rowObj = commandBean.getRowObject();
            if (rowObj instanceof XEquiptParameter) {
                XEquiptInstance xInstance = (XEquiptInstance) primary;
                XEquiptParameter xParameter = (XEquiptParameter) rowObj;
                xParameter.setValue(value);
                xParameter = PersistenceHelper.service().save(xParameter);

                IPrimitiveType<?> primitiveType = xParameter.getBaseType().getPrimitive(xParameter.getValue());
                ThingworxHelper.thingworx().setPropertyValue(xInstance.getNumber(), xParameter.getNumber(), primitiveType);

                formResult.setData(xParameter);
            }
        }

        return formResult;
    }
}
