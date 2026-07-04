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
import com.flame.util.XException;

import xw.auths.entity.XGroup;
import xw.auths.entity.XGroupUserLink;
import xw.auths.entity.XUser;

public class XUserDataLoader extends AbstractDataLoader {

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
			String password = data.getAttribute("password");
			String tel = data.getAttribute("tel");
			String fax = data.getAttribute("fax");
			String email = data.getAttribute("email");
			String address = data.getAttribute("address");
			String postalCode = data.getAttribute("postalCode");
			String thingModel = data.getAttribute("thingModel");
			XUser ftuser = null;
			if (list.isEmpty()) {
				ftuser = new XUser();
				ftuser.setNumber(number);
				ftuser.setName(name);
				ftuser.setFullName(fullName);
				ftuser.setEnglishName(englishName);
				ftuser.setDescription(description);
				ftuser.setPassword(password);
				ftuser.setTel(tel);
				ftuser.setFax(fax);
				ftuser.setEmail(email);
				ftuser.setAddress(address);
				ftuser.setPostalCode(postalCode);
				ftuser.setAppKey(UUID.randomUUID().toString());
				IThingModel userModel = ThingModelHelper.manager().getThingModel(thingModel);
				ftuser.setThingModel(userModel);
				ftuser = PersistenceHelper.service().save(ftuser);
			} else {
				ftuser = (XUser) list.get(0);
				ftuser.setName(name);
				ftuser.setFullName(fullName);
				ftuser.setEnglishName(englishName);
				ftuser.setDescription(description);
				ftuser.setPassword(password);
				ftuser.setTel(tel);
				ftuser.setFax(fax);
				ftuser.setEmail(email);
				ftuser.setAddress(address);
				ftuser.setPostalCode(postalCode);
				ftuser.setAppKey(UUID.randomUUID().toString());
				IThingModel userModel = ThingModelHelper.manager().getThingModel(thingModel);
				ftuser.setThingModel(userModel);
				ftuser = PersistenceHelper.service().save(ftuser);
			}
			
			if (ftuser == null)
				continue;

			for (FlameDataLoad.Link link : data.getLink()) {
				List<?> qr = this.queryObject(link.getWhere());
				if (qr.isEmpty()) {
					throw new XException("Object ----");
				}
				XObject persist = (XObject) qr.get(0);

				List<?> _qr = this.queryLink(link.getClazz(), persist, ftuser);
				XGroupUserLink itemLink = null;
				if (!_qr.isEmpty()) {
					itemLink = (XGroupUserLink) _qr.get(0);
				}
				if (itemLink == null) {
					itemLink = XGroupUserLink.newGroupUserLink((XGroup) persist, ftuser);
				}
				itemLink = PersistenceHelper.service().save(itemLink);

			}
		}
	}

}
