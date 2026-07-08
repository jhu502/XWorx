package com.xworx.method;

import com.flame.config.basic.BasicConfiguration;
import com.flame.logs.XPrintStream;
import com.flame.util.XConstants;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.zookeeper.ZookeeperAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.lang.management.ManagementFactory;

@EnableCaching
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.flame.config", "com.flame.common", "com.flame.orm"}, exclude = {SecurityAutoConfiguration.class, ZookeeperAutoConfiguration.class})
@EnableTransactionManagement //开启事务的注解
public class XWorxMethodApplication extends SpringBootServletInitializer implements CommandLineRunner {
    static {
        if (!(System.out instanceof XPrintStream))
            System.setOut(new XPrintStream(System.out));

        System.setProperty(XConstants.XWORX_JVM_ID, ManagementFactory.getRuntimeMXBean().getName());
        /**
         * zookeeper-config由bootstrap ApplicationContext来加载，因此需在bootstrap.properites去进行设置，或者在System属性中去设置；
         * 下面设置是禁止xworx manager去自动启动zookeeper配置中心
         */
        System.setProperty("spring.cloud.zookeeper.enabled", Boolean.FALSE.toString());
    }

    public void run(String... args) throws Exception {
        String[] beanNames = BasicConfiguration.getBeanNamesForType(ServletWebServerFactory.class);
        BasicConfiguration.getBean(beanNames[0], ServletWebServerFactory.class);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 导出 WebSocket 端点，注册所有使用 @ServerEndpoint 注解的 WebSocket 处理器
     * 这是启用 WebSocket 功能的必要配置
     * 
     * @return ServerEndpointExporter 实例，用于自动扫描并注册 WebSocket 端点
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    public static void main(String[] args) {
        SpringApplication xMethod = new SpringApplication(XWorxMethodApplication.class);
        xMethod.run(args);
        /**
         * 拦截调用System.exit(0)后导致Eclipse停止SpringBoot失败的问题;
         * ThingWorx停止时会调用ThingWorxBootstrapper.contextDestroyed()->ThingWorxServer.getInstance().shutDownPlatform()->System.exit(0);
         * 导致SpringBoot加载ThingWorx后不能够正常的在Eclipse中停止;
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
