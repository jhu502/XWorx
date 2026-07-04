package com.thing.database;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.thing.common.AbstractConnection;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnection extends AbstractConnection<FTDatabase> implements DataSource {
	private HikariDataSource dataSource;

	@Override
	public boolean startConnection(FTDatabase config) {
		HikariConfig hkConfig = new HikariConfig();
		hkConfig.setPoolName(config.getNumber() + "," + config.getName());
		hkConfig.setDriverClassName(config.getDriverClass());
		hkConfig.setJdbcUrl(config.getJdbcUrl());
		hkConfig.setUsername(config.getUserName());
		hkConfig.setPassword(config.getPassword());
		hkConfig.setMaximumPoolSize(config.getMaxConnections());

		String connectionTest = config.getConnectionTest();
		if (connectionTest != null && !"".equals(connectionTest.trim())) {
			hkConfig.setConnectionTestQuery(connectionTest);
		}
		this.dataSource = new HikariDataSource(hkConfig);

		return true;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return this.dataSource.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.dataSource.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		this.dataSource.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return this.dataSource.getLoginTimeout();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return this.dataSource.getParentLogger();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return this.dataSource.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.dataSource.isWrapperFor(iface);
	}

	@Override
	public void close() throws IOException {
		this.dataSource.close();
	}

	public boolean isClosed() {
		return this.dataSource.isClosed();
	}

	@Override
	public Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return this.dataSource.getConnection(username, password);
	}

}
