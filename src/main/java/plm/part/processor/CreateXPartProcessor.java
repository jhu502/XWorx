package plm.part.processor;

import xw.context.entity.Container;
import xw.context.entity.XFolder;
import plm.part.XPart;
import plm.part.XPartUsageLink;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;

public class CreateXPartProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		String number = commandBean.getTextParameter("number");
		String name = commandBean.getTextParameter("name");
		XObject context = commandBean.getPrimaryObj();
		
		XPart xpart = XPart.newInstance(number, name);
		
		if (context instanceof XFolder) {
			XFolder folder = (XFolder) context;
			Container container = folder.getContainer();
			xpart.setContainer(container);
			xpart = PersistenceHelper.service().save(xpart);
		} else if (context instanceof Container) {
			Container container = (Container) context;
			xpart.setContainer(container);
			xpart = PersistenceHelper.service().save(xpart);
		} else if (context instanceof XPart) {
			XPart parent = (XPart) context;
			Container container = parent.getContainer();
			xpart.setContainer(container);
			xpart = PersistenceHelper.service().save(xpart);
			XPartUsageLink usageLink = XPartUsageLink.newInstance(parent, xpart.getMaster());
			usageLink.setQuantity(1);
			usageLink.setUnit("EA");
			usageLink = PersistenceHelper.service().save(usageLink);
		}
		
		formResult.setData(xpart);

		return formResult;
	}
}
