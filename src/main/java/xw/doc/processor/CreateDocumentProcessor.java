package xw.doc.processor;

import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.xui.XCommandBean;
import xw.context.entity.Container;
import xw.context.entity.XFolder;
import xw.doc.XDocument;
import xw.doc.XDocumentUsageLink;

public class CreateDocumentProcessor extends DefaultFormProcessor {
	@Override
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		String number = commandBean.getTextParameter("number");
		String name = commandBean.getTextParameter("name");
		XObject context = commandBean.getPrimaryObj();
		
		XDocument document = XDocument.newInstance(number, name);
		
		if (context instanceof XFolder) {
			XFolder folder = (XFolder) context;
			Container container = folder.getContainer();
			document.setContainer(container);
			document = PersistenceHelper.service().save(document);
		} else if (context instanceof Container) {
			Container container = (Container) context;
			document.setContainer(container);
			document = PersistenceHelper.service().save(document);
		} else if (context instanceof XDocument) {
			XDocument parent = (XDocument) context;
			Container container = parent.getContainer();
			document.setContainer(container);
			document = PersistenceHelper.service().save(document);
			XDocumentUsageLink usageLink = XDocumentUsageLink.newInstance(parent, document.getMaster());
			usageLink = PersistenceHelper.service().save(usageLink);
		}
		
		formResult.setData(document);

		return formResult;
	}
}
