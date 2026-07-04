package com.thing.entity;

import com.flame.annotations.XConfig;
import com.flame.annotations.XDefinition;
import com.flame.type.XBaseType;
import com.thing.runtime.ConnectableThing;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.sql.Timestamp;

@MappedSuperclass
@XDefinition(name = "Connectable", config = ConnectableThing.class, icon = "images/connected.png", description = "Connectable", display = "Connectable", en_US = "Connectable", zh_CN = "可连接")
public abstract class ConnectableEntity extends ModeledEntity {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "enable_monitor")
	@XConfig(name = "enableMonitor", friendlyName = "Enable Monitor", baseType = XBaseType.BOOLEAN, description = "Enable Monitor")
	private boolean enableMonitor = false;
	@Basic
	@Column(name = "monitor_rate")
	@XConfig(name = "monitorRate", friendlyName = "Monitor Rate", baseType = XBaseType.INTEGER, description = "Monitor Rate", defaultValue = "100")
	private Integer monitorRate = 100;
	@Basic
	@Column(name = "number_of_retries")
	@XConfig(name = "numberOfRetries", friendlyName = "Number of Retries", baseType = XBaseType.INTEGER, description = "Number of Retries", defaultValue = "5")
	private Integer numberOfRetries = 5;
	@Basic
	@Column(name = "connected")
	@XConfig(name = "connected", friendlyName = "Connected", baseType = XBaseType.BOOLEAN, description = "Connected", created = false, modified = false)
	private boolean connected = false;
	@Basic
	@Column(name = "last_connection")
	@XConfig(name = "lastConnection", friendlyName = "Last Connection Time", baseType = XBaseType.DATETIME, description = "Last Connection Time", created = false, modified = false)
	private Timestamp lastConnection;

	public Boolean isEnableMonitor() {
		return enableMonitor;
	}

	public void setEnableMonitor(Boolean enableMonitor) {
		this.enableMonitor = enableMonitor;
	}

	public Integer getMonitorRate() {
		return monitorRate;
	}

	public void setMonitorRate(Integer monitorRate) {
		this.monitorRate = monitorRate;
	}

	public Integer getNumberOfRetries() {
		return numberOfRetries;
	}

	public void setNumberOfRetries(Integer numberOfRetries) {
		this.numberOfRetries = numberOfRetries;
	}
	
	public Boolean isConnected() {
		return connected;
	}

	public void setConnected(Boolean connected) {
		this.connected = connected;
	}

	public Timestamp getLastConnection() {
		return lastConnection;
	}

	public void setLastConnection(Timestamp lastConnection) {
		this.lastConnection = lastConnection;
	}
}
