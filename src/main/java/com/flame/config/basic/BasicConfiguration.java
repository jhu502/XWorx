package com.flame.config.basic;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.annotation.Configuration;

import com.flame.config.FlameConfiguration;
import com.flame.util.FlameUtils;
import com.flame.util.XConstants;

@Configuration
public class BasicConfiguration extends FlameConfiguration {
    private static CuratorFramework framework;
    private static String xworxHome;

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

    public static String getXWHome() {
        if (FlameUtils.isBlank(xworxHome)) {
            xworxHome = System.getProperty(XConstants.XWORX_HOME);
        }
        if (FlameUtils.isBlank(xworxHome)) {
            xworxHome = BasicConfiguration.getProperty(XConstants.XWORX_HOME);
        }
        return xworxHome;
    }

    public static CuratorFramework getFramework() {
        if (framework == null) {
            framework = getBean(CuratorFramework.class);
        }
        return framework;
    }
}
