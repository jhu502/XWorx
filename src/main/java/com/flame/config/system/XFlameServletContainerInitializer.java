package com.flame.config.system;

import java.util.Set;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HandlesTypes;

import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.web.WebApplicationInitializer;

@HandlesTypes(WebApplicationInitializer.class)
public class XFlameServletContainerInitializer implements ServletContainerInitializer {

	@Override
	public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
		System.out.println("-----------------------------------------------------XXXX---");
		org.apache.catalina.startup.ContextConfig config;
		org.springframework.boot.web.embedded.tomcat.TomcatWebServer server;
		ServletWebServerApplicationContext gk;
	}

}
