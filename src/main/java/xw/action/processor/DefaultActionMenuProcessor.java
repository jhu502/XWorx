package xw.action.processor;

import java.util.ArrayList;
import java.util.List;

import com.flame.action.ActionKey;
import com.flame.action.IAction;
import com.flame.action.IActionItem;
import com.flame.action.IActionModel;
import com.flame.xui.XCommandBean;
import com.flame.common.form.DefaultFormProcessor;
import com.flame.common.form.FormResult;
import com.flame.xui.XUIAction;

import xw.action.service.XActionServiceHelper;

public class DefaultActionMenuProcessor extends DefaultFormProcessor {
	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		List<XUIAction> result = new ArrayList<>();
		ActionKey actionKey = commandBean.getActionKey();
		if (actionKey != null) {
			List<IAction> list = XActionServiceHelper.service().getSubActions(actionKey.getName(), actionKey.getType());
			for (IAction action : list) {
				if (action instanceof IActionItem) {
					XUIAction xaction = XUIAction.toXUIAction((IActionItem) action);
					result.add(xaction);
				} else if (action instanceof IActionModel) {
					XUIAction xaction = XUIAction.toXUIAction((IActionModel) action);
					result.add(xaction);
				}
			}
		}
		formResult.setData(result);

		return formResult;
	}

}
