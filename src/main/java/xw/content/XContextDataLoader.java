package xw.content;

import java.util.List;

import com.flame.auths.SessionHelper;
import com.flame.loader.AbstractDataLoader;
import com.flame.loader.FlameDataLoad;
import com.flame.orm.PersistenceHelper;
import com.flame.util.XException;

import xw.auths.entity.XUser;
import xw.context.ContextHelper;
import xw.context.entity.XCompany;
import xw.context.entity.XOrganization;
import xw.context.entity.XSite;

public class XContextDataLoader extends AbstractDataLoader {

	@Override
	public void executeLoad(FlameDataLoad flameLoad) throws Exception {
		for (FlameDataLoad.LoadObject loadObject : flameLoad.getData()) {
			Class<?> clazz = loadObject.getClazz();
			String identity = loadObject.getWhere();
			Object value = loadObject.getAttributes().get(identity);
			if (XSite.class.equals(clazz)) {
				List<?> list = this.queryObject(clazz, new Object[][] { { identity, value } });
				if (!list.isEmpty())
					continue;
				XSite xsite = new XSite();
				xsite.setNumber(loadObject.getAttribute("number"));
				xsite.setName(loadObject.getAttribute("name"));
				xsite.setDescription(loadObject.getAttribute("description"));
				xsite.setCreator((XUser) SessionHelper.getCurrentUser());
				PersistenceHelper.service().save(xsite);
			} else if (XOrganization.class.equals(clazz)) {
				List<?> list = this.queryObject(clazz, new Object[][] { { identity, value } });
				if (!list.isEmpty())
					continue;
				XOrganization xOrgan = new XOrganization();
				xOrgan.setNumber(loadObject.getAttribute("number"));
				xOrgan.setName(loadObject.getAttribute("name"));
				xOrgan.setDescription(loadObject.getAttribute("description"));
				xOrgan.setCreator((XUser) SessionHelper.getCurrentUser());
				String domainNo = loadObject.getAttribute("domain");
				XSite xsite = ContextHelper.repository().getXSite();
				xOrgan.setContainer(xsite);
				PersistenceHelper.service().save(xOrgan);
			} else if (XCompany.class.equals(clazz)) {
				List<?> list = this.queryObject(clazz, new Object[][] { { identity, value } });
				if (!list.isEmpty())
					continue;
				XCompany company = new XCompany();
				company.setNumber(loadObject.getAttribute("number"));
				company.setName(loadObject.getAttribute("name"));
				company.setDescription(loadObject.getAttribute("description"));
				List<?> containers = this.queryObject(XSite.class, new Object[][] { { "number", "SITE" } });
				if (containers.isEmpty())
					throw new XException("Site container isn't found.");
				company.setContainer((XSite) containers.get(0));
				company.setCreator((XUser) SessionHelper.getCurrentUser());
				PersistenceHelper.service().save(company);
			}
		}

	}

}
