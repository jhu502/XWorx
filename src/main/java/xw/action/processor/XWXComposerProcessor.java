package xw.action.processor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.flame.action.ActionKey;
import com.flame.action.IAction;
import com.flame.action.IActionModel;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.common.form.FormStatus;
import com.flame.xui.XUIAction;

import xw.action.entity.XActionModel;
import xw.action.service.XActionServiceHelper;

public class XWXComposerProcessor extends DefaultFormProcessor {
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		ActionKey actionKey = commandBean.getActionKey();
		if (actionKey == null) {
			formResult.setStatus(FormStatus.FAILURE);
		} else {
			IActionModel actionModel = XActionServiceHelper.service().getActionModel(actionKey.getName(), actionKey.getType());
			if (actionModel != null) {
				Map<String, XUIAction> globalMap = new HashMap<>();
				Map<String, XUIAction> result = this.generateModelMenu(actionModel, globalMap, actionKey.toString());

				formResult.setData(result.values());
			}
		}
		formResult.setStatus(FormStatus.SUCCESS);

		return formResult;
	}

	protected Map<String, XUIAction> generateModelMenu(IActionModel actionModel, Map<String, XUIAction> globalMap, String relatedKey) {
		List<IAction> actionList = XActionServiceHelper.service().getSubActions(actionModel);
		Map<String, XUIAction> result = new LinkedHashMap<>();
		for (IAction action : actionList) {
			XUIAction uiAction = XUIAction.toXUIAction(action);
			uiAction.setSource(relatedKey);

			String ukey = uiAction.getWinType() + "$" + uiAction.getKey();
			if (!globalMap.containsKey(ukey)) {
				result.put(ukey, uiAction);
				if (action instanceof XActionModel) {
					XActionModel _actionModel = (XActionModel) action;
					Map<String, XUIAction> resultMap = this.generateModelMenu(_actionModel, globalMap, relatedKey);
					for (XUIAction actionBean : resultMap.values()) {
						uiAction.addChild(actionBean);
					}
				}
			}
		}

		return result;
	}
}
