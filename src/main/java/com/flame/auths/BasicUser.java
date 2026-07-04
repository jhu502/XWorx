package com.flame.auths;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class BasicUser implements IAuthUser {
	private static final long serialVersionUID = 1L;
	private String name = "";
	protected String password = "";
	protected boolean expired = false;
	protected boolean locked = false;
	protected boolean enabled = true;
	private transient Collection<GrantedAuthority> authorities;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return this.getName();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void authorities(Collection<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return !this.expired;
	}

	public boolean isExpired() {
		return this.expired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !this.locked;
	}

	public boolean isLocked() {
		return this.locked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public String toString() {
		return this.getUsername();
	}
}
