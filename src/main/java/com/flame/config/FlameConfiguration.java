package com.flame.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;

import com.flame.util.FlameUtils;
import com.flame.util.XConstants;

import jakarta.annotation.Resource;

@Configuration
public class FlameConfiguration implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static Environment environment;
    private static String scheme;
    private static String domain;
    private static String context;

    @Resource
    public void setApplicationContext(ApplicationContext appContext) {
        applicationContext = appContext;
    }

    @Resource
    public void setEnvironment(Environment environment) {
        FlameConfiguration.environment = environment;
    }

    @Value("${server.servlet.scheme}")
    public void setScheme(String _protocol) {
        scheme = _protocol;
    }

    @Value("${server.servlet.domain}")
    public void setDomain(String _domain) {
        domain = _domain;
    }

    @Value("${server.servlet.context-path}")
    public void setContext(String _context) {
        context = _context;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static String getScheme() {
        return scheme;
    }

    public static String getHttpURL(String uri) {
        if (FlameUtils.isBlank(uri)) {
            return getScheme() + "://" + getDomain();
        } else {
            return getScheme() + "://" + getDomain() + (uri.startsWith("/") ? uri : "/" + uri);
        }
    }

    public static String getWebsocketURL(String uri) {
        String baseUrl = FlameUtils.equalsIgnoreCase(getScheme(), "https") ? "wss" : "ws" + "://" + getDomain();
        if (FlameUtils.isBlank(uri)) {
            return baseUrl;
        } else {
            return baseUrl + (uri.startsWith("/") ? uri : "/" + uri);
        }
    }

    public static String getDomain() {
        return domain;
    }

    public static String getContext() {
        return context;
    }

    public static String getProperty(String key) {
        if (environment == null) {
            environment = getBean(Environment.class);
        }

        if (environment == null)
            return "";

        return environment.getProperty(key);
    }

    public static void regSingletonBean(String beanName, Object singletonBean) {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getAutowireCapableBeanFactory();
            defaultListableBeanFactory.registerSingleton(beanName, singletonBean);
        }
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null)
            return null;

        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    public static String[] getBeanDefinitionNames() {
        return applicationContext.getBeanDefinitionNames();
    }

    public static String[] getBeanNamesForType(@Nullable Class<?> type) {
        return applicationContext.getBeanNamesForType(type);
    }
}
