package com.flame.action;

import com.flame.xui.WinType;

public interface IActionItem extends IAction {
	String getIconCls();

	void setIconCls(String iconCls);

	void setUrl(String url);

	String getProcessor();

	void setProcessor(String processor);

	String getOnclick();

	void setOnclick(String onclick);

	String getBeforeJS();

	void setBeforeJS(String beforejs);

	String getAfterJS();

	void setAfterJS(String afterjs);

	WinType getWinType();

	void setWinType(WinType winType);
}
