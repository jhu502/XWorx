package xw.auths;

import java.util.List;
import java.util.UUID;

import com.flame.loader.AbstractDataLoader;
import com.flame.loader.FlameDataLoad;
import com.flame.loader.FlameDataLoad.LoadObject;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.thing.IThingModel;
import com.flame.thing.ThingModelHelper;
import com.flame.util.FlameUtils;
import com.flame.util.XException;

import xw.auths.entity.GroupTypeRB;
import xw.auths.entity.XGroup;
import xw.auths.entity.XGroupGroupLink;
import xw.domain.DomainHelper;
import xw.domain.entity.XAdminDomain;

public class XGroupDataLoader extends AbstractDataLoader {

	@Override
	public void executeLoad(FlameDataLoad dataLoad) throws Exception {
		for (LoadObject data : dataLoad.getData()) {
			Class<?> clazz = data.getClazz();
			String identity = data.getWhere();
			Object value = data.getAttributes().get(identity);
			List<?> list = this.queryObject(clazz, new Object[][] { { identity, value } });

			String number = data.getAttribute("number");
			String name = data.getAttribute("name");
			String fullName = data.getAttribute("fullName");
			String englishName = data.getAttribute("englishName");
			String description = data.getAttribute("description");
			String tel = data.getAttribute("tel");
			String fax = data.getAttribute("fax");
			String email = data.getAttribute("email");
			String address = data.getAttribute("address");
			String postalCode = data.getAttribute("postalCode");
			String groupType = data.getAttribute("groupType");
			String domainRef = data.getAttribute("domain");
			String scope = data.getAttribute("scope");
			String secretRequired = data.getAttribute("secretRequired");
			String resourceIds = data.getAttribute("resourceIds");
			String grantTypes = data.getAttribute("grantTypes");
			String redirectUri = data.getAttribute("redirectUri");
			if (FlameUtils.isBlank(domainRef)) {
				throw new XException("请填写domain信息!");
			}
			List<?> domains = DomainHelper.repository().getDomainByNumber(domainRef);
			if (domains == null || domains.size() == 0) {
				throw new XException("Domain:" + domainRef + " 不存在!");
			}
			XAdminDomain domain = (XAdminDomain) domains.get(0);
			String thingModel = data.getAttribute("thingModel");
			GroupTypeRB GType = GroupTypeRB.toGroupTypeRB(groupType);
			XGroup xgroup = null;
			if (list.isEmpty()) {
				xgroup = new XGroup();
				xgroup.setNumber(number);
				xgroup.setName(name);
				xgroup.setFullName(fullName);
				xgroup.setEnglishName(englishName);
				xgroup.setDescription(description);
				xgroup.setFax(fax);
				xgroup.setTel(tel);
				xgroup.setEmail(email);
				xgroup.setAddress(address);
				xgroup.setPostalCode(postalCode);
				xgroup.setGroupType(GType);
				xgroup.setAdminDomain(domain);
				xgroup.setScope(scope);
				xgroup.setSecretRequired(Boolean.parseBoolean(secretRequired));
				xgroup.setResourceIds(resourceIds);
				xgroup.setGrantTypes(grantTypes);
				xgroup.setRedirectUri(redirectUri);
				xgroup.setAppKey(UUID.randomUUID().toString());
				IThingModel groupModel = ThingModelHelper.manager().getThingModel(thingModel);
				xgroup.setThingModel(groupModel);
				xgroup = PersistenceHelper.service().save(xgroup);
			} else {
				xgroup = (XGroup) list.get(0);
				xgroup.setName(name);
				xgroup.setFullName(fullName);
				xgroup.setEnglishName(englishName);
				xgroup.setDescription(description);
				xgroup.setFax(fax);
				xgroup.setTel(tel);
				xgroup.setEmail(email);
				xgroup.setAddress(address);
				xgroup.setPostalCode(postalCode);
				xgroup.setGroupType(GType);
				xgroup.setAdminDomain(domain);
				xgroup.setAppKey(UUID.randomUUID().toString());
				xgroup.setScope(scope);
				xgroup.setSecretRequired(Boolean.parseBoolean(secretRequired));
				xgroup.setResourceIds(resourceIds);
				xgroup.setGrantTypes(grantTypes);
				xgroup.setRedirectUri(redirectUri);
				IThingModel groupModel = ThingModelHelper.manager().getThingModel(thingModel);
				xgroup.setThingModel(groupModel);
				xgroup = PersistenceHelper.service().save(xgroup);
			}
			
			if (xgroup == null)
				continue;

			for (FlameDataLoad.Link link : data.getLink()) {
				List<?> qr = this.queryObject(link.getWhere());
				if (qr.isEmpty()) {
					throw new XException("Not found Object:" + link.getWhere());
				}
				XObject persist = (XObject) qr.get(0);

				List<?> _qr = this.queryLink(link.getClazz(), persist, xgroup);
				XGroupGroupLink itemLink = null;
				if (!_qr.isEmpty()) {
					itemLink = (XGroupGroupLink) _qr.get(0);
				}
				if (itemLink == null) {
					itemLink = XGroupGroupLink.newGroupGroupLink((XGroup) persist, xgroup);
				}
				itemLink = PersistenceHelper.service().save(itemLink);
			}
		}
	}

}
