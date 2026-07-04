package mes.apl.processor;

import mes.equipt.XEquiptInstance;
import com.thing.worx.ThingworxHelper;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.type.XInfoTable;
import com.flame.type.XValueCollection;

import java.util.List;

public class PushEquiptInstanceProcessor extends DefaultFormProcessor {

    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);

        List<Object> rowObjs = commandBean.getRowObjects();
        for (Object rowObject : rowObjs) {
            if (rowObject instanceof XEquiptInstance) {
                XEquiptInstance equiptInstance = (XEquiptInstance) rowObject;

                XInfoTable xTable = ThingworxHelper.thingworx().invokeThingService(equiptInstance.getNumber(), "PushParams2Machine", new XValueCollection());
                //List<?> list = XPLineServiceHelper.service().getEquiptParameter(equiptInstance);
                //for (Object object : list) {
                //    XEquiptParameter equipParam = (XEquiptParameter) object;
                //}

                formResult.setData(equiptInstance);
            }
        }
        return formResult;
    }
}
