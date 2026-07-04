package mes;

import com.flame.config.basic.BasicConfiguration;
import com.flame.logs.XPrintStream;
import com.flame.util.XConstants;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.lang.management.ManagementFactory;

@EnableWebSecurity
@SpringBootApplication(scanBasePackages = {"com.flame.config", "mes"}, exclude = {SecurityAutoConfiguration.class})
@EnableTransactionManagement //开启事务的注解
public class XWorxMESApplication extends SpringBootServletInitializer implements CommandLineRunner {
    static {
        if (!(System.out instanceof XPrintStream))
            System.setOut(new XPrintStream(System.out));

        System.setProperty(XConstants.XWORX_JVM_ID, ManagementFactory.getRuntimeMXBean().getName());
    }

    public void run(String... args) throws Exception {
        String[] beanNames = BasicConfiguration.getBeanNamesForType(ServletWebServerFactory.class);
        BasicConfiguration.getBean(beanNames[0], ServletWebServerFactory.class);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication plmApplication = new SpringApplication(XWorxMESApplication.class);
        plmApplication.run(args);
    }

}
