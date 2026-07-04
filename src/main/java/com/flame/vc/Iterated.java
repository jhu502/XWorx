package com.flame.vc;

import com.flame.orm.XPersistable;

public interface Iterated<T extends Master> extends XPersistable {
	CheckOutInfo getCheckOutInfo();

	void setCheckOutInfo(CheckOutInfo cio);

	boolean isLatest();

	void setLatest(boolean latest);

	T getMaster();

	void setMaster(T master);
}
