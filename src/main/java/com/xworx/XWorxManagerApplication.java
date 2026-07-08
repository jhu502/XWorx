package com.xworx;

import java.lang.management.ManagementFactory;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.zookeeper.ZookeeperAutoConfiguration;
import org.springframework.context.annotation.Import;

import com.flame.logs.XPrintStream;
import com.flame.util.XConstants;
import com.xworx.config.XWorxAutoImportSelector;
import com.xworx.config.XWorxManagerListener;

@EnableCaching
@Import(XWorxAutoImportSelector.class) //在@SpringBootApplication中启用excludeName的模糊匹配功能
@SpringBootApplication( //Spring Boot应用的入口类注解
        exclude = {ZookeeperAutoConfiguration.class}, //禁止XManager自动配置Zookeeper
        scanBasePackages = {"com.xworx.config", "com.alibaba.arthas.tunnel"}, //启动Arthas tunnel Server
        excludeName = {"org.flowable.*"} //禁止XManager去启动Flowable
)
public class XWorxManagerApplication extends SpringBootServletInitializer implements ApplicationRunner {
    private Thread shutdownHook = Thread.currentThread();

    static {
        if (!(System.out instanceof XPrintStream)) System.setOut(new XPrintStream(System.out));

        System.setProperty(XConstants.XWORX_JVM_ID, ManagementFactory.getRuntimeMXBean().getName());
        /**
         * zookeeper-config由bootstrap ApplicationContext来加载，因此需在bootstrap.properites去进行设置，或者在System属性中去设置；
         * 下面设置是禁止xworx manager去自动启动zookeeper配置中心
         */
        System.setProperty("spring.cloud.zookeeper.enabled", Boolean.FALSE.toString());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.registerShutdownHook();
    }

    public void registerShutdownHook() {
        if (this.shutdownHook == null) {
            this.shutdownHook = new Thread() {
                @Override
                public void run() {
                    synchronized (shutdownHook) {
                    }
                }
            };
        }
        synchronized (this.shutdownHook) {
            try {
                this.shutdownHook.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication xManager = new SpringApplication(XWorxManagerApplication.class);
        xManager.addListeners(new XWorxManagerListener());
        xManager.run(args);
    }
}