package mes.apl.processor;

import xw.context.entity.Container;
import mes.apl.XAssemblyLine;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;

public class CreateProductionLineProcessor extends DefaultFormProcessor {

    @Override
    public FormResult doOperation(XCommandBean commandBean) {
        FormResult formResult = super.doOperation(commandBean);
        String number = commandBean.getTextParameter("number");
        String name = commandBean.getTextParameter("name");
        String description = commandBean.getTextParameter("description");

        Container container = (Container) PersistenceHelper.service().find("OR:xw.container.entity.XSite:10");
        XAssemblyLine xpline = XAssemblyLine.newAssemblyLine(number, name, description);
        xpline.setContainer(container);
        xpline = PersistenceHelper.service().save(xpline);
        formResult.setData(xpline);

        return formResult;
    }
}
