package com.xworx.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractProcessMonitor {
	protected Log logger = LogFactory.getLog(AbstractProcessMonitor.class);
	protected static final String SEP = File.separator;
	protected Map<String, XWorxProcessRunable> xprocessMap = new HashMap<>();

	public void addMonitorProcess(String key, XWorxProcessRunable monitorProcess) {
		this.xprocessMap.put(key, monitorProcess);
	}
}
