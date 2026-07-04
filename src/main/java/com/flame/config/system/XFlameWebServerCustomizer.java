package com.flame.config.system;

import java.io.File;

import com.flame.util.XConstants;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.CatalinaBaseConfigurationSource;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

import com.flame.config.basic.BasicConfiguration;
import com.flame.util.FlameUtils;

@Configuration
public class XFlameWebServerCustomizer implements WebServerFactoryCustomizer<WebServerFactory> {
	private static final Logger logger = LoggerFactory.getLogger(XFlameWebServerCustomizer.class);
	@Value("${ajp.port:8011}")
	private int ajpPort = -1;

	public int getAjpPort() {
		return ajpPort;
	}

	public void setAjpPort(int ajpPort) {
		this.ajpPort = ajpPort;
	}

	@Override
	public void customize(WebServerFactory factory) {
		TomcatServletWebServerFactory tomcatFactory = (TomcatServletWebServerFactory) factory;
		
		if (this.ajpPort > 0) {
			this.configAjpConnector(tomcatFactory);
		}

		if (FlameUtils.isBlank(BasicConfiguration.getXWHome())) {
			System.setProperty(XConstants.XWORX_HOME, System.getProperty("user.dir"));
		}
		String catalina_base = BasicConfiguration.getXWHome() + File.separator + XConstants.XWORX_TOMCAT;
		FlameUtils.mkdirs(catalina_base);
		String catalina_home = catalina_base;
		System.setProperty("catalina.base", catalina_base);
		System.setProperty("catalina.home", catalina_home);
		String work_base = catalina_home + File.separator + "instances" + File.separator + "instance-" + tomcatFactory.getPort();
		tomcatFactory.setBaseDirectory(new File(work_base));
		String serverXml = catalina_base + File.separator + "conf" + File.separator + "server.xml";
		ConfigFileLoader.setSource(new CatalinaBaseConfigurationSource(new File(work_base), serverXml));

		/**
		 * SpringBoot通过Jar模式时，是不支持jsp的，只有在War模式才会支持jsp；
		 * 但是在SpringBoot的Development模式，它有个固定的目录：$Project/java/main/webapp去支持在SpringBoot环境进行jsp开发；
		 * 因此为了使SpringBoot运行在Standalone模式能够支持jsp，就需要覆盖prepareContext()方法去实现；
		 * -----------------------------------------------------------------------------------------------
		 * 为context-path:/XWorx的StandardContext设置docBase目录，SpringBoot的embedded tomcat将从该目录读取文件资源：css,js,jsp,image,html,xml etc；
		 */
		String $CODEBASE = BasicConfiguration.getXWHome() + File.separator + "codebase";
		tomcatFactory.setDocumentRoot(new File($CODEBASE));
		logger.info("XWorx web application running on " + $CODEBASE);
	}

	/**
	 * SpringBoot的嵌入式Web容器是默认是不会启动AJP连接器, 若需要使用Ajp需要代码去启用
	 * @param factory
	 */
	public void configAjpConnector(TomcatServletWebServerFactory factory) {
		logger.info("XWorx Ajp connector port:" + this.getAjpPort());
		Connector ajpConnector = new Connector("org.apache.coyote.ajp.AjpNioProtocol");
		ajpConnector.setPort(this.getAjpPort());
		ajpConnector.setSecure(false);
		ajpConnector.setEnableLookups(false);
		ajpConnector.setMaxPostSize(-1);
		ajpConnector.setMaxSavePostSize(8388608);
		ajpConnector.setMaxParameterCount(-1);
		ajpConnector.setUseBodyEncodingForURI(true);
		ajpConnector.setURIEncoding("UTF-8");
		ajpConnector.setProperty("backlog", "100");
		ajpConnector.setProperty("packetSize", "8192");
		ajpConnector.setProperty("maxThreads", "500");
		ajpConnector.setProperty("minSpareThreads", "8");
		ajpConnector.setProperty("secretRequired", "false");
		factory.addAdditionalTomcatConnectors(ajpConnector);
	}

}
