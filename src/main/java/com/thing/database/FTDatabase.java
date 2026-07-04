package com.thing.database;

import com.flame.type.ServiceType;
import com.flame.annotations.XConfig;
import com.flame.annotations.XDefinition;
import com.flame.annotations.XService;
import com.flame.type.XBaseType;
import com.thing.entity.ConnectableEntity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "FTDatabase", uniqueConstraints = {})
@XDefinition(name = "FTDatabase", config = FTDatabaseThing.class, icon = "images/database.png", description = "FTDatabase", display = "Database", en_US = "Database", zh_CN = "数据库", pageUri = "/freemarker/thing/database/ftdatabaseThing")
public class FTDatabase extends ConnectableEntity {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "db_category", length = 50)
	@XConfig(name = "dbCategory", friendlyName = "Database Category", baseType = XBaseType.STRING, description = "Database Category", required = true)
	protected String dbCategory = "";
	@Basic
	@Column(name = "driver_class", length = 100)
	@XConfig(name = "driverClass", friendlyName = "Driver Class", baseType = XBaseType.STRING, description = "Driver Class", required = true)
	protected String driverClass = "";
	@Basic
	@Column(name = "jdbc_url", length = 100)
	@XConfig(name = "jdbcUrl", friendlyName = "Database Jdbc Url", baseType = XBaseType.STRING, description = "Database Jdbc Url", required = true)
	protected String jdbcUrl = "";
	@Basic
	@Column(name = "username", length = 100)
	@XConfig(name = "userName", friendlyName = "Database User Name", baseType = XBaseType.STRING, description = "Database User Name", required = true)
	protected String userName = "";
	@Basic
	@Column(name = "password", length = 100)
	@XConfig(name = "password", friendlyName = "Database Password", baseType = XBaseType.PASSWORD, description = "Database Password", required = true)
	protected String password = "";
	@Basic
	@Column(name = "max_connections")
	@XConfig(name = "maxConnections", friendlyName = "Maximum Number of Connections", baseType = XBaseType.INTEGER, description = "Maximum number of connections", defaultValue = "5")
	protected Integer maxConnections = 5;
	@Basic
	@Column(name = "connection_test", length = 200)
	@XConfig(name = "connectionTest", friendlyName = "Connection Test Query", baseType = XBaseType.STRING, description = "Connection Test Query", defaultValue = "SELECT NOW()")
	protected String connectionTest = "SELECT NOW()";
	
	@XService(name = "getDbCategory", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getDbCategory() {
		return dbCategory;
	}

	public void setDbCategory(String dbCategory) {
		this.dbCategory = dbCategory;
	}

	@XService(name = "getDriverClass", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	@XService(name = "getJdbcUrl", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getJdbcUrl() {
		return this.jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	@XService(name = "getUserName", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@XService(name = "getPassword", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@XService(name = "getMaxConnections", serviceType = ServiceType.Local, resultType = XBaseType.INTEGER)
	public Integer getMaxConnections() {
		return this.maxConnections;
	}

	public void setMaxConnections(Integer maxConnections) {
		this.maxConnections = maxConnections;
	}

	@XService(name = "getConnectionTest", serviceType = ServiceType.Local, resultType = XBaseType.STRING)
	public String getConnectionTest() {
		return connectionTest;
	}

	public void setConnectionTest(String connectionTest) {
		this.connectionTest = connectionTest;
	}

}
