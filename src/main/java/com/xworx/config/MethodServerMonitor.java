package com.xworx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import jakarta.annotation.Resource;

/**
 * @Order 注解主要用来控制配置类的加载顺序：数字越小, 越先加载；
 * 在XManager中，有时候@Order好像不生效，可以能的原因时SpringBoot没生成@Order的代理，解决办法就是实现Ordered接口即可
 */
@Configuration
@Order(400)
public class MethodServerMonitor extends AbstractProcessMonitor implements ApplicationRunner, Ordered {
	@Value("${com.xworx.MethodServer.enabled:false}")
	private boolean enableMethodServer = false;
	@Value("${com.xworx.MethodServer.count}")
	private int msLauncherCount;
	@Value("${com.xworx.MethodServer.timeout:20000}")
	private int startTimeout = 20000;
	@Resource
	private Environment environment;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (this.enableMethodServer) {
			for (int i = 1; i <= msLauncherCount; i++) {
				String methodLauncherCommond = environment.getProperty("com.xworx.MethodServer.launcher.command." + i);
				logger.info(methodLauncherCommond);
				XWorxProcessRunable processRunable = XWorxProcessRunable.newInstance(methodLauncherCommond, XProcessType.Method, i);
				processRunable.start();
				this.addMonitorProcess("com.xworx.MethodServer.launcher.command." + i, processRunable);
				Thread.sleep(startTimeout);
			}
		}
	}

	/**
	 * Zookeeper、Ignite、Method服务进程要依次启动，@Order好像不生效，但通过实现Ordered接口后就能生效
	 */
	@Override
	public int getOrder() {
		Order order = MethodServerMonitor.class.getAnnotation(Order.class);
		if (order == null)
			return Integer.MAX_VALUE;
		else
			return order.value();
	}
}
