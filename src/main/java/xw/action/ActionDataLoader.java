package xw.action;

import com.flame.util.FlameUtils;
import com.flame.xui.WinType;
import xw.action.entity.AbstractAction;
import xw.action.entity.XActionItem;
import xw.action.entity.XActionItemLink;
import xw.action.entity.XActionModel;
import com.flame.loader.AbstractDataLoader;
import com.flame.loader.FlameDataLoad;
import com.flame.loader.FlameDataLoad.LoadObject;
import com.flame.orm.PersistenceHelper;
import com.flame.orm.XObject;
import com.flame.util.XException;

import java.lang.reflect.Constructor;
import java.util.List;

public class ActionDataLoader extends AbstractDataLoader {

	@Override
	public void executeLoad(FlameDataLoad dataLoad) throws Exception {
		for (LoadObject data : dataLoad.getData()) {
			List<?> list = this.queryObject(data);
			AbstractAction authItem = null;
			if (!list.isEmpty()) {
				authItem = (AbstractAction) list.get(0);
			} else {
				Constructor<?> constructor = data.getClazz().getConstructor();
				authItem = (AbstractAction) constructor.newInstance();
			}

			if (authItem == null)
				continue;

			if (authItem instanceof XActionItem) {
				XActionItem action = (XActionItem) authItem;
				action.setName(data.getAttribute("name"));
				action.setType(data.getAttribute("type"));
				action.setSupportedType(data.getAttribute("supported_type"));
				action.setDisplay(data.getAttribute("display"));
				action.setIcon(data.getAttribute("icon"));
				action.setIconCls(data.getAttribute("iconCls"));
				action.setStyle(data.getAttribute("style"));
				action.setUrl(data.getAttribute("url"));
				action.setProcessor(data.getAttribute("processor"));
				action.setOnclick(data.getAttribute("onclick"));
				action.setBeforeJS(data.getAttribute("beforeJS"));
				action.setAfterJS(data.getAttribute("afterJS"));
				String winType = data.getAttribute("winType");
				if (FlameUtils.isNotBlank(winType)) {
					action.setWinType(WinType.valueOf(winType));
				}
				action.setEn_US(data.getAttribute("en_US"));
				action.setZh_CN(data.getAttribute("zh_CN"));
			} else if (authItem instanceof XActionModel) {
				XActionModel model = (XActionModel) authItem;
				model.setName(data.getAttribute("name"));
				model.setType(data.getAttribute("type"));
				model.setSupportedType(data.getAttribute("supported_type"));
				model.setDisplay(data.getAttribute("display"));
				model.setProcessor(data.getAttribute("processor"));
				model.setIcon(data.getAttribute("icon"));
				model.setStyle(data.getAttribute("style"));
				model.setEn_US(data.getAttribute("en_US"));
				model.setZh_CN(data.getAttribute("zh_CN"));
			}
			authItem = PersistenceHelper.service().save(authItem);

			List<FlameDataLoad.Link> links = data.getLink();
			for (FlameDataLoad.Link link : links) {
				List<?> qr = this.queryObject(link.getWhere());
				if (qr.isEmpty()) {
					throw new XException("Not found Object:" + link.getWhere());
				}
				XObject persist = (XObject) qr.get(0);
				/**
				 * left表示当前新创建的对象在Link的左边
				 */
				if ("left".equals(link.getRefer())) {
					List<?> _qr = this.queryLink(link.getClazz(), authItem, persist);
					XActionItemLink itemLink = null;
					if (!_qr.isEmpty()) {
						itemLink = (XActionItemLink) _qr.get(0);
					}
					if (itemLink == null) {
						itemLink = XActionItemLink.newActionItemLink((XActionModel) authItem, (AbstractAction) persist);
					}
					itemLink.setSort(link.getAttribute("sort"));
					PersistenceHelper.service().save(itemLink);
				} else if ("right".equals(link.getRefer())) {
					List<?> _qr = this.queryLink(link.getClazz(), persist, authItem);
					XActionItemLink itemLink = null;
					if (!_qr.isEmpty()) {
						itemLink = (XActionItemLink) _qr.get(0);
					}
					if (itemLink == null) {
						itemLink = XActionItemLink.newActionItemLink((XActionModel) persist, authItem);
					}
					itemLink.setSort(link.getAttribute("sort"));
					PersistenceHelper.service().save(itemLink);
				}
			}
		}
	}
}
