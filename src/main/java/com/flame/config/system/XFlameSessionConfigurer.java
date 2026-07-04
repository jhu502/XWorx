package com.flame.config.system;

import com.flame.auths.BasicUser;
import com.flame.auths.ISession;
import com.flame.auths.IUser;
import com.flame.auths.SessionHelper;
import com.flame.config.basic.BasicConfiguration;
import com.flame.util.FlameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Configuration
public class XFlameSessionConfigurer implements ISession {
    protected static final Logger logger = LoggerFactory.getLogger(XFlameSessionConfigurer.class);
    private UserDetailsService userService;

    @Override
    public IUser currentUser() {
        logger.debug("currentUser: {}", Thread.currentThread().getName());
        Authentication authenticator = SecurityContextHolder.getContext().getAuthentication();
        if (authenticator == null) {
            throw new AuthenticationServiceException("Current session is not Authentication.");
        }

        IUser currentUser = null;
        Object principal = authenticator.getPrincipal();
        if (principal instanceof IUser) {
            currentUser = (IUser) principal;
        } else {
            currentUser = new BasicUser();
            currentUser.setName((String) principal);
        }

        return currentUser;
    }

    public IUser setCurrentUser(String name) {
        logger.debug("setCurrentUser: {}", Thread.currentThread().getName());
        IUser currentUser = null;
        try {
            currentUser = SessionHelper.getCurrentUser();
        } catch (AuthenticationServiceException e) {
            currentUser = this.getUserByName(name);
            if (currentUser == null) {
                throw new AuthenticationServiceException("User:" + name + " is not found.");
            }

            Collection<? extends GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("Normal"));
            AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(UUID.randomUUID().toString(), currentUser, authorities);
            SecurityContextHolder.getContext().setAuthentication(anonymousToken);

        }
        if (currentUser != null && !FlameUtils.equals(currentUser.getName(), name)) {
            throw new AuthenticationServiceException("Switching users is prohibited..");
        }
        return currentUser;
    }

    public IUser getUserByName(String userName) {
        if (FlameUtils.isEmpty(userName)) {
            return null;
        }

        if (userService == null) {
            userService = BasicConfiguration.getBean(UserDetailsService.class);
        }

        return (IUser) userService.loadUserByUsername(userName);
    }
}
