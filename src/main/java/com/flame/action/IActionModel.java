package com.flame.action;

public interface IActionModel extends IAction {
	String getType();
	
	String getName();

	String getProcessor();

	void setProcessor(String processor);
}
