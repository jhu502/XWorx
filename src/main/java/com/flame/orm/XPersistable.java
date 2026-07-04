package com.flame.orm;

import java.io.Serializable;

public interface XPersistable extends Serializable {
	long getXid();

	default String getXclass() {
		return this.getClass().getName();
	}

	default String getOid() {
		return "OR:" + this.getXclass() + ":" + this.getXid();
	}

	default String getDisplay() {
		return this.getOid();
	}
}
