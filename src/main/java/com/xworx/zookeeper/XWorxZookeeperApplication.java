package com.xworx.zookeeper;

import java.lang.management.ManagementFactory;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.flame.logs.XPrintStream;
import com.flame.util.XConstants;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, excludeName = {"org.flowable.*"})
public class XWorxZookeeperApplication implements ApplicationRunner {
    static {
        if (!(System.out instanceof XPrintStream))
            System.setOut(new XPrintStream(System.out));

        System.setProperty(XConstants.XWORX_JVM_ID, ManagementFactory.getRuntimeMXBean().getName());
        /**
         * zookeeper-config由bootstrap ApplicationContext来加载，因此需在bootstrap.properites去进行设置，或者在System属性中去设置；
         * 下面设置是禁止xworx zookeeper去自动启动zookeeper配置中心
         */
        System.setProperty("spring.cloud.zookeeper.enabled", Boolean.FALSE.toString());
        System.setProperty("spring.flowable.security.enabled", Boolean.FALSE.toString());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
    }

    public static void main(String[] args) {
        SpringApplication.run(XWorxZookeeperApplication.class, args);

        /**
         * -拦截调用System.exit(0)后导致Eclipse停止SpringBoot失败的问题;
         * -ThingWorx停止时会调用ThingWorxBootstrapper.contextDestroyed()->ThingWorxServer.getInstance().shutDownPlatform()->System.exit(0);
         * -导致SpringBoot加载ThingWorx后不能够正常的在Eclipse中停止;
         */
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
