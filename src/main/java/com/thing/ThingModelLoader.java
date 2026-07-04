package com.thing;

import java.util.List;

import com.flame.loader.AbstractDataLoader;
import com.flame.loader.FlameDataLoad;
import com.flame.loader.FlameDataLoad.LoadObject;
import com.flame.localize.LocalizationBean;
import com.flame.orm.PersistenceHelper;
import com.flame.thing.ThingModelHelper;
import com.flame.util.XException;
import com.thing.entity.XThingModel;

public class ThingModelLoader extends AbstractDataLoader {

	@Override
	public void executeLoad(FlameDataLoad dataLoad) throws Exception {
		for (LoadObject loadObj : dataLoad.getData()) {
			Class<?> clazz = loadObj.getClazz();
			String identity = loadObj.getWhere();
			Object value = loadObj.getAttributes().get(identity);
			List<?> list = this.queryObject(clazz, new Object[][] { { identity, value } });
			if (list.isEmpty()) {
				String number = loadObj.getAttribute("number");
				String name = loadObj.getAttribute("name");
				String icon = loadObj.getAttribute("icon");
				String description = loadObj.getAttribute("description");
				String pageUri = loadObj.getAttribute("pageUri");
				String entity = loadObj.getAttribute("entity");
				String model = loadObj.getAttribute("model");
				String baseModel = loadObj.getAttribute("baseModel");
				baseModel = baseModel == null ? "" : baseModel.toUpperCase();
				String en_US = loadObj.getAttribute("en_US");
				String zh_CN = loadObj.getAttribute("zh_CN");
				LocalizationBean localBean = new LocalizationBean();
				localBean.setEn_US(en_US);
				localBean.setZh_CN(zh_CN);

				if (baseModel == null || "".equals(baseModel.trim())) {
					ThingModelHelper.manager().createThingModel(number.toUpperCase(), name, icon, description, pageUri, entity, model, localBean, null);
				} else {
					List<?> _list = this.queryObject(clazz, new Object[][]{{"number", baseModel}});
					if (_list.size() > 0) {
						XThingModel _baseModel = (XThingModel) _list.get(0);
						ThingModelHelper.manager().createThingModel(number, name, icon, description, pageUri, entity, model, localBean, _baseModel);
					} else {
						throw new XException("Base Model:" + baseModel + "不存在.");
					}
				}
			} else {
				String icon = loadObj.getAttribute("icon");
				String description = loadObj.getAttribute("description");
				String pageUri = loadObj.getAttribute("pageUri");
				String entity = loadObj.getAttribute("entity");
				String model = loadObj.getAttribute("model");
				XThingModel thingModel = (XThingModel) list.get(0);
				thingModel.setIcon(icon);
				thingModel.setPageUri(pageUri);
				thingModel.setDescription(description);
				thingModel.setEntity(entity);
				thingModel.setModel(model);
				PersistenceHelper.service().save(thingModel);
			}
		}
	}
}
