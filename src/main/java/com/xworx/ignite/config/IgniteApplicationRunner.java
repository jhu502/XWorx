package com.xworx.ignite.config;

import java.io.File;
import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @Order 注解主要用来控制配置类的加载顺序：数字越小，越先加载；
 * 在XWorxManager中，@Order好像不生效，但是实现Ordered接口后就生效了
 */
@Configuration
@Order(200)
public class IgniteApplicationRunner implements ApplicationRunner, Ordered {
	private final String SEP = File.separator;
	@Value("${xworx.home}")
	private String xworx_home;

	public void run(ApplicationArguments args) throws Exception {
		String igniteConfig = xworx_home + SEP + "codebase" + SEP + "config" + SEP + "xworxconfig-ignite.xml";
		Method mainMethod = Class.forName("org.apache.ignite.startup.cmdline.CommandLineStartup").getMethod("main", String[].class);
		mainMethod.invoke(null, new Object[] { new String[] { igniteConfig } });
	//	org.springframework.boot.context.properties.ConfigurationBeanFactoryMetadata x;
	}

	@Override
	public int getOrder() {
		Order order = IgniteApplicationRunner.class.getAnnotation(Order.class);
		if (order == null)
			return Integer.MAX_VALUE;
		else
			return order.value();
	}
}
