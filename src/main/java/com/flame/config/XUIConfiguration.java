package com.flame.config;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.flame.xui.XCommandBean;
import com.flame.util.XException;
import com.flame.xui.XUIComponent;
import com.flame.xui.service.IComponentBuilder;

@Configuration
@ComponentScan({ "com.flame.xui.service" })
public class XUIConfiguration {
	protected static final Logger logger = LoggerFactory.getLogger(XUIConfiguration.class);

	public IComponentBuilder buildComponentConfig(String uibuilder) {
		if (uibuilder == null || "".equals(uibuilder))
			return null;

		try {
			/**
			 * -使用线程上下文的ClassLoader去加载类，打破 ServiceLoader.load(service, cl);
			 */
			Class<?> builderClass = Thread.currentThread().getContextClassLoader().loadClass(uibuilder);
			Constructor<?> constructor = builderClass.getConstructor();
			IComponentBuilder builder = (IComponentBuilder) constructor.newInstance();

			return builder;
		} catch (XException e) {
			throw e;
		} catch (Exception e) {
			throw new XException(e);
		}
	}

	public XUIComponent genComponentConfig(String uibuilder, XCommandBean commandBean) {
		IComponentBuilder builder = buildComponentConfig(uibuilder);

		return builder.buildComponentConfig(commandBean);
	}

	public Object genComponentData(String uibuilder, XCommandBean commandBean) {
		IComponentBuilder builder = buildComponentConfig(uibuilder);

		return builder.buildComponentData(commandBean);
	}
}
