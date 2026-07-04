package com.flame.auths;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public interface IAuthUser extends IUser, UserDetails {
    void authorities(Collection<GrantedAuthority> authorities);
}
