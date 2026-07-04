package xw.domain;

import com.flame.loader.AbstractDataLoader;
import com.flame.loader.FlameDataLoad;
import com.flame.loader.FlameDataLoad.LoadObject;
import com.flame.logical.LogicalUtils;
import com.flame.orm.ObjectReference;
import com.flame.orm.PersistenceHelper;
import com.flame.util.FlameUtils;
import xw.context.entity.Container;
import xw.domain.entity.XAdminDomain;

import java.util.List;

public class DomainDataLoader extends AbstractDataLoader {

	@Override
	public void executeLoad(FlameDataLoad flameLoad) {
		for (LoadObject loadObject : flameLoad.getData()) {
			Class<?> clazz = loadObject.getClazz();
			String identity = loadObject.getWhere();
			Object value = loadObject.getAttributes().get(identity);
			if (XAdminDomain.class.equals(clazz)) {
				List<?> list = this.queryObject(clazz, new Object[][]{{identity, value}});
				if (!list.isEmpty())
					continue;

				XAdminDomain domain = new XAdminDomain();
				domain.setNumber(loadObject.getAttribute("number"));
				domain.setName(loadObject.getAttribute("name"));
				domain.setDescription(loadObject.getAttribute("description"));
				String parent = loadObject.getAttribute("upperDomain");
				String contextRef = loadObject.getAttribute("contextRef");
				Object result = LogicalUtils.getObject(contextRef);

				if (result instanceof Container) {
					domain.setContextRef(ObjectReference.newObjectReference((Container) result));
				} else if (result instanceof List) {
					List<?> containers = (List<?>) result;
					if (!containers.isEmpty()) {
						domain.setContextRef(ObjectReference.newObjectReference((Container) containers.get(0)));
					}
				}

				if (FlameUtils.isNotBlank(parent)) {
					List<?> _list = this.queryObject(clazz, new Object[][]{{identity, parent}});
					if (!_list.isEmpty()) {
						XAdminDomain domainRef = (XAdminDomain) _list.get(0);
						domain.setAdminDomain(domainRef);
					}
				}

				domain = PersistenceHelper.service().save(domain);
			}
		}
	}
}
