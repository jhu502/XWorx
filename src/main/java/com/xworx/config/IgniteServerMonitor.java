package com.xworx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @Order 注解主要用来控制配置类的加载顺序：数字越小，越先加载；
 * 在XWorxManager中，有时候@Order好像不生效，可以能的原因时SpringBoot没生成@Order的代理，解决办法就是实现Ordered接口即可
 */
@Configuration
@Order(300)
public class IgniteServerMonitor extends AbstractProcessMonitor implements ApplicationRunner, Ordered {
	@Value("${com.xworx.IgniteServer.enabled:false}")
	private boolean enableIgniteServer = false;
	@Value("${com.xworx.IgniteServer.launcher.command}")
	private String igniteLauncherCommand;
	@Value("${com.xworx.IgniteServer.timeout:15000}")
	private int startTimeout = 15000;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (this.enableIgniteServer) {
			logger.debug(igniteLauncherCommand);
			XWorxProcessRunable processRunable = XWorxProcessRunable.newInstance(igniteLauncherCommand, XProcessType.Ignite, 0);
			processRunable.start();
			this.addMonitorProcess("com.xworx.IgniteServer.launcher.command", processRunable);
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
