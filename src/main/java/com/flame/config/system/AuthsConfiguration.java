package com.flame.config.system;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.flame.config.basic.BasicConfiguration;

import xw.auths.repos.XAuthRepository;

/**
 * Activiti的安全认证依赖UserDetailsService实现
 * @author JIHU
 */
@Configuration
@ComponentScan({ "xw.auths.service", "xw.auths.controller" })
@EntityScan({ "xw.auths.entity" })
@EnableJpaRepositories({ "xw.auths.repos" })
public class AuthsConfiguration implements UserDetailsService {
	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
	private XAuthRepository xuserRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (xuserRepository == null) {
			xuserRepository = BasicConfiguration.getBean(XAuthRepository.class);
		}
		return xuserRepository.findByNameIgnoreCase(username);
	}
}
