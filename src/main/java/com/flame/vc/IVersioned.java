package com.flame.vc;

public interface IVersioned<T extends Master> extends Iterated<T> {

	String getVersion();

	void setVersion(String version);
}
