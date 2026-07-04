package mes.apl.processor;

import xw.context.entity.Container;
import mes.equipt.XEquipment;
import mes.equipt.XEquiptInstance;
import mes.equipt.XEquiptInstanceLink;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;

public class CreateEquiptInstanceProcessor extends DefaultFormProcessor {

    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        String number = commandBean.getTextParameter("number");
        String name = commandBean.getTextParameter("name");
        String description = commandBean.getTextParameter("description");

        XObject primary = commandBean.getPrimaryObj();
        if (primary instanceof XEquipment) {
            XEquipment equipment = (XEquipment) primary;
            Container container = (Container) PersistenceHelper.service().find("OR:xw.container.entity.XSite:10");
            XEquiptInstance equiptInstance = XEquiptInstance.newXEquiptInstance(number, name, description);
            equiptInstance.setContainer(container);
            equiptInstance = PersistenceHelper.service().save(equiptInstance);
            XEquiptInstanceLink instanceLink = XEquiptInstanceLink.newXEquiptInstanceLink(equipment, equiptInstance);
            PersistenceHelper.service().save(instanceLink);

            formResult.setData(equiptInstance);
        }

        return formResult;
    }
}
