package com.flame.action;

import com.flame.localize.ILocalization;

public interface IAction extends ILocalization {
	String getName();

	void setName(String name);

	String getType();

	void setType(String type);

	String getActionKey();

	String getIcon();

	void setIcon(String icon);

	String getUrl();

	String getStyle();

	void setStyle(String style);

	String getSupportedType();

	void setSupportedType(String supportedType);
	
	String getDisplay();
}
