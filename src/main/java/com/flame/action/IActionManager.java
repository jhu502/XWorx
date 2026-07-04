package com.flame.action;

import java.util.List;

import com.flame.orm.XPersistable;

public interface IActionManager {
	public IActionItem getActionItem(String name, String type);
	
	public IAction getAction(String name, String type);
	
	public List<IAction> getSubActions(IActionModel actionModel);
	
	public List<IAction> getSubActions(String name, String type);
	
	public List<IAction> getActions(String identity, XPersistable persist);
}
