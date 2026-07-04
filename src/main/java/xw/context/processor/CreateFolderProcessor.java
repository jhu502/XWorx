package xw.context.processor;

import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.auths.SessionHelper;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;

import xw.auths.entity.XUser;
import xw.context.entity.Container;
import xw.context.entity.XFolder;

public class CreateFolderProcessor extends DefaultFormProcessor {
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);

		XObject primaryObj = commandBean.getPrimaryObj();
		if (primaryObj instanceof Container) {
			Container container = (Container) primaryObj;
			XFolder folder = new XFolder();
			folder.setName(commandBean.getTextParameter("name"));
			folder.setContainer(container);
			folder.setAdminDomain(container.getAdminDomain());
			folder.setCreator((XUser) SessionHelper.getCurrentUser());
			folder = PersistenceHelper.service().save(folder);
			formResult.setData(folder);
		} else if (primaryObj instanceof XFolder) {
			XFolder foldering = (XFolder) primaryObj;
			Container container = foldering.getContainer();
			XFolder folder = new XFolder();
			folder.setName(commandBean.getTextParameter("name"));
			folder.setFolder(foldering);
			folder.setContainer(container);
			folder.setAdminDomain(container.getAdminDomain());
			folder.setCreator((XUser) SessionHelper.getCurrentUser());
			folder = PersistenceHelper.service().save(folder);
			formResult.setData(folder);
		}

		return formResult;
	}
}
