package com.xworx.ignite;

import java.lang.management.ManagementFactory;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.flame.logs.XPrintStream;
import com.flame.util.XConstants;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class }, excludeName = { "org.flowable.*" })
@ComponentScan({ "com.xworx.ignite", "com.xworx.ignite.config" })
public class XWorxIgniteApplication implements ApplicationRunner {
	static {
		if (!(System.out instanceof XPrintStream))
			System.setOut(new XPrintStream(System.out));
		
		System.setProperty(XConstants.XWORX_JVM_ID, ManagementFactory.getRuntimeMXBean().getName());
		/**
		 * zookeeper-config由bootstrap ApplicationContext来加载，因此需在bootstrap.properites去进行设置，或者在System属性中去设置；
		 * 下面设置是禁止xworx ignite去自动启动zookeeper配置中心
		 */
		System.setProperty("spring.cloud.zookeeper.enabled", Boolean.FALSE.toString());
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
	}

	public static void main(String[] args) {
		SpringApplication.run(XWorxIgniteApplication.class, args);

		//	System.setSecurityManager(new SecurityManager() {
		//		@Override
		//		public void checkPermission(Permission perm) {
		//		}
		//
		//		@Override
		//		public void checkPermission(Permission perm, Object context) {
		//		}
		//
		//		@Override
		//		public void checkExit(int status) {
		//			super.checkExit(status);
		//			throw new SecurityException();
		//		}
		//	});
	}
}
