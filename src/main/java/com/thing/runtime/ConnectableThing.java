package com.thing.runtime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flame.thing.IModelManaged;
import com.thing.common.AbstractThingModel;
import com.thing.common.IConnectable;
import com.thing.common.IConnection;

public class ConnectableThing<T extends IModelManaged, S extends IConnection<?>> extends AbstractThingModel<T> implements IConnectable<S> {
	/**
	 * IThingEntity主动建立的连接，例如：JDBC、SSH、FTP、SMTP、IMAP等等，这些类型的
	 * 连接需要Thing主动去发起并启动连接，IThingEntity与IConnection是一一对应.
	 */
	@JsonIgnore
	private transient S connection;

	public ConnectableThing(T target) {
		super(target);
	}

	@Override
	public void setConnection(S connection) {
		this.connection = connection;
	}

	@JsonIgnore
	@Override
	public S getConnection() {
		return this.connection;
	}

	@Override
	public boolean isConnected() {
		if (this.connection == null) {
			return false;
		} else {
			return true;
		}
	}

	protected void initializeConnectableThing() throws Exception {
	}

	protected void startConnectableThing() {
	}

	protected void stopConnectableThing() {
	}

	protected void onConnection() throws Exception {
	}

	public final void startThing() {
		super.startThing();

		if (!this.isConnected()) {
			this.startConnectableThing();
		}
	}

	public void stopThing() {
		super.stopThing();

		if (this.isConnected()) {
			this.stopConnectableThing();
		}
	}
}
