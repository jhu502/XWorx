package xw.action.processor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.flame.action.IAction;
import com.flame.action.IActionModel;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.xui.XUIAction;

import xw.action.service.XActionServiceHelper;

public class XWXNavigatorProcessor extends DefaultFormProcessor {
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		IActionModel actionModel = XActionServiceHelper.service().getActionModel("XWX-Navigator", "XWX-Navigator");
		if (actionModel == null) {
			return formResult;
		} else {
			Map<String, XUIAction> globalMap = new HashMap<>();
			Map<String, XUIAction> sortedMap = new LinkedHashMap<>();

			List<IAction> actionList = XActionServiceHelper.service().getSubActions(actionModel);
			for (IAction action : actionList) {
				XUIAction uiAction = XUIAction.toXUIAction(action);
				String ukey = uiAction.getWinType() + "$" + uiAction.getKey();
				if (!globalMap.containsKey(ukey)) {
					sortedMap.put(ukey, uiAction);
				}

				if (action instanceof IActionModel) {
					IActionModel model = (IActionModel) action;
					List<IAction> actions = XActionServiceHelper.service().getSubActions(model);
					uiAction.addChildren(actions.stream().map(e -> XUIAction.toXUIAction(e)).collect(Collectors.toList()));
				}
			}
			formResult.setData(sortedMap.values());
		}
		return formResult;
	}
}
