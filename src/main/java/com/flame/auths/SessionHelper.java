package com.flame.auths;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.flame.config.basic.BasicConfiguration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionHelper {
	protected static final Logger logger = LoggerFactory.getLogger(SessionHelper.class);
	private static ISession isession;

	public static HttpSession getSession() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (null != requestAttributes) {
			HttpServletRequest request = requestAttributes.getRequest();
			HttpSession session = request.getSession();
			return session;
		}

		return null;
	}

	public static Locale getLocale() {
		return LocaleContextHolder.getLocale();
	}

	public static String getUserName(HttpSession httpSession) {
		if (httpSession == null)
			return null;

		SecurityContext securityContext = (SecurityContext) httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
		if (securityContext == null)
			return null;

		Authentication auths = securityContext.getAuthentication();
		if (auths == null)
			return null;

		return auths.getName();
	}
	
	public static IUser getCurrentUser() {
		if (isession == null) {
			isession = BasicConfiguration.getBean(ISession.class);
		}
		
		return isession.currentUser();
	}

	public static IUser setCurrentUser(String name) {
		if (isession == null) {
			isession = BasicConfiguration.getBean(ISession.class);
		}

		return isession.setCurrentUser(name);
	}

	public static IUser getUserByName(String userName) {
		if (isession == null) {
			isession = BasicConfiguration.getBean(ISession.class);
		}

		return isession.getUserByName(userName);
	}
}
