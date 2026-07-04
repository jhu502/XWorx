package com.flame.auths;

public interface ICreatorInfo<T extends IUser> {
	public T getCreator();

	public void setCreator(T creator);

	public default String getCreatorName() {
		if (this.getCreator() == null)
			return "";

		return this.getCreator().getName();
	}
}
