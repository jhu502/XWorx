package com.flame.xui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.util.FlameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class XUIComponent extends AbstractComponent {
	protected static final Logger logger = LoggerFactory.getLogger(XUIComponent.class);
	private String randomId = FlameUtils.getRandomConst().toString();
	private String compId = "";
	private String method = "post";
	private String url = "";
	private String component = "";
	private Map<String, Object> queryParams = new HashMap<>();

	public String getCompId() {
		return compId;
	}

	public void setCompId(String compId) {
		this.compId = compId;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public Map<String, Object> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(Map<String, Object> queryParams) {
		this.queryParams = queryParams;
	}

	public String getRandomId() {
		return this.randomId;
	}

	@JsonIgnore
	public abstract List<String> fields();
}
