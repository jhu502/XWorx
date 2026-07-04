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

public class XWXActionMenuProcessor extends DefaultFormProcessor {
	private static final String ACTION_MENU = "XWX-ActionMenu";

	public FormResult doOperation(XCommandBean commandBean) {
		FormResult formResult = super.doOperation(commandBean);
		List<XUIAction> result = new ArrayList<>();
		ActionKey actionKey = commandBean.getActionKey(ACTION_MENU);
		if (actionKey != null) {
			List<IAction> actionList = XActionServiceHelper.service().getSubActions(actionKey.getName(), actionKey.getType());
			for (IAction iAction : actionList) {
				XUIAction xuiAction = buildXUIAction(iAction);
				if (xuiAction != null) {
					result.add(xuiAction);
				}
			}
		}
		formResult.setData(result);
		return formResult;
	}

	private XUIAction buildXUIAction(IAction action) {
		if (action instanceof IActionItem) {
			return XUIAction.toXUIAction((IActionItem) action);
		} else if (action instanceof IActionModel) {
			IActionModel model = (IActionModel) action;
			XUIAction xaction = XUIAction.toXUIAction(model);
			List<IAction> children = XActionServiceHelper.service().getSubActions(model);
			for (IAction child : children) {
				XUIAction childAction = buildXUIAction(child);
				if (childAction != null) {
					xaction.addChild(childAction);
				}
			}
			return xaction;
		}
		return null;
	}
}
