package com.flame.type;

public enum ServiceType {
	Javascript("images/service_js.png"),
	Remote("images/service_remote.png"),
	Local("images/service_local.png"),
	SQL("images/service_sql.png");
	
	private String icon;
	
	ServiceType(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return this.name();
	}
	
	public String getIcon() {
		return this.icon;
	}
}
