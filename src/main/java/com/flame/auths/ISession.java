package com.flame.auths;

public interface ISession {
	IUser currentUser();

	IUser setCurrentUser(String name);

	IUser getUserByName(String userName);
}
