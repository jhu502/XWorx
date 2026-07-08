package com.xworx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @Order 注解主要用来控制配置类的加载顺序：数字越小，越先加载；
 * 在XManager中，有时候@Order好像不生效，可以能的原因时SpringBoot没生成@Order的代理，解决办法就是实现Ordered接口即可
 */
@Configuration
@Order(100)
public class MinioServerMonitor extends AbstractProcessMonitor implements ApplicationRunner, Ordered {
	@Value("${com.xworx.MinioServer.enabled:false}")
	private boolean enableMinioServer = false;
	@Value("${com.xworx.MinioServer.launcher.command}")
	private String minioLauncherCommand;
	@Value("${com.xworx.MinioServer.timeout:5000}")
	private int startTimeout = 5000;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (this.enableMinioServer) {
			logger.debug(minioLauncherCommand);
			XWorxProcessRunable processRunable = XWorxProcessRunable.newInstance(minioLauncherCommand, XProcessType.Ignite, 0);
			processRunable.start();
			this.addMonitorProcess("com.xworx.MinioServer.launcher.command", processRunable);
			Thread.sleep(startTimeout);
		}
	}

	/**
	 * Zookeeper、Ignite、Method服务进程要依次启动，@Order好像不生效，但通过实现Ordered接口后就能生效
	 */
	@Override
	public int getOrder() {
		Order order = IgniteServerMonitor.class.getAnnotation(Order.class);
		if (order == null)
			return Integer.MAX_VALUE;
		else
			return order.value();
	}
}
