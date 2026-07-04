package com.thing.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flame.type.ServiceType;
import com.flame.annotations.XParam;
import com.flame.annotations.XService;
import com.flame.type.XBaseType;
import com.flame.type.XInfoTable;
import com.flame.type.IPrimitiveType;
import com.flame.util.XException;
import com.thing.runtime.ConnectableThing;

public class FTDatabaseThing extends ConnectableThing<FTDatabase, DatabaseConnection> {
	private static Logger logger = LoggerFactory.getLogger(FTDatabaseThing.class);

	public FTDatabaseThing(FTDatabase target) {
		super(target);
	}

	protected void startConnectableThing() {
		FTDatabase entity = this.getThingEntity();

		DatabaseConnection dbconnect = new DatabaseConnection();
		dbconnect.startConnection(entity);
		this.setConnection(dbconnect);
	}

	protected void stopConnectableThing() {
		if (this.getConnection() == null) {
			throw new XException("Thing未连接!");
		}
		
		try {
			this.getConnection().close();
			this.setConnection(null);
		} catch (Exception eClosePool) {
			logger.error("Error Closing Connection Pool in ");
		}
	}

	@Override
	public boolean isConnected() {
		if (this.getConnection() == null) {
			return false;
		} else {
			return !this.getConnection().isClosed();
		}
	}

	@XService(serviceType = ServiceType.Local, name = "query", resultType = XBaseType.INFOTABLE, params = { @XParam(name = "sql", type = XBaseType.STRING) })
	public XInfoTable query(String sql) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			if (this.getConnection() == null) {
				throw new XException("Thing未连接!");
			}
			connection = this.getConnection().getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			IPrimitiveType<?> iprimitive = (IPrimitiveType<?>) XBaseType.INFOTABLE.getPrimitive(resultSet);
			XInfoTable infoTable = (XInfoTable) iprimitive.getValue();
			return infoTable;
		} catch (SQLException e) {
			throw new XException(e);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
				} catch (SQLException e) {
					throw new XException(e);
				} finally {
					try {
						if (connection != null) {
							connection.close();
						}
					} catch (SQLException e) {
						throw new XException(e);
					}
				}
			}
		}
	}
}
