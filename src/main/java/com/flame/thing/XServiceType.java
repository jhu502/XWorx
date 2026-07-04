package com.flame.thing;

public enum XServiceType {
	Javascript("images/service_js.png"),
	Remote("images/service_remote.png"),
	Local("images/service_local.png"),
	SQL("images/service_sql.png");
	
	private String icon;
	
	XServiceType(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return this.name();
	}
	
	public String getIcon() {
		return this.icon;
	}
}
