package com.flame.auths;

public class TestUser implements IUser {
	private String name;
	private String password;
	
	public static TestUser newInstance(String name) {
		TestUser testUser = new TestUser();
		testUser.setName(name);
		testUser.setName(name);
		
		return testUser;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return this.password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

}
