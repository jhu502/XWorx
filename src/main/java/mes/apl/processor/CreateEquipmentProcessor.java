package mes.apl.processor;

import mes.apl.XAssemblyLineUsageLink;
import xw.context.entity.Container;
import mes.equipt.XEquipment;
import mes.apl.XAssemblyLine;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;

public class CreateEquipmentProcessor extends DefaultFormProcessor {
    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        String number = commandBean.getTextParameter("number");
        String name = commandBean.getTextParameter("name");
        String description = commandBean.getTextParameter("description");

        XObject primary = commandBean.getPrimaryObj();
        if (primary instanceof XAssemblyLine) {
            XAssemblyLine xpline = (XAssemblyLine) primary;
            Container container = (Container) PersistenceHelper.service().find("OR:xw.container.entity.XSite:10");
            XEquipment xequipt = XEquipment.newXEquipment(number, name, description);
            xequipt.setContainer(container);
            xequipt = PersistenceHelper.service().save(xequipt);
            XAssemblyLineUsageLink usageLink = XAssemblyLineUsageLink.newXAssemblyLineUsageLink(xpline, xequipt.getMaster());
            PersistenceHelper.service().save(usageLink);

            formResult.setData(xequipt);
        }

        return formResult;
    }
}
