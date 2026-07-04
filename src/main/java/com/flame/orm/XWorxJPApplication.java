package com.flame.orm;

import java.lang.management.ManagementFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.flame.logs.XPrintStream;
import com.flame.util.XConstants;

@SpringBootApplication(scanBasePackages = { "com.flame.config", "com.flame.orm" }, exclude = { SecurityAutoConfiguration.class })
public class XWorxJPApplication extends SpringBootServletInitializer implements CommandLineRunner {
    static {
        if (!(System.out instanceof XPrintStream))
            System.setOut(new XPrintStream(System.out));

        System.setProperty(XConstants.XWORX_JVM_ID, ManagementFactory.getRuntimeMXBean().getName());
    }

    @Override
    public void run(String... args) throws Exception {
    }

}
