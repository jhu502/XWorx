package plm;

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

import com.flame.config.basic.BasicConfiguration;

@EnableWebSecurity
@SpringBootApplication(scanBasePackages = {"com.flame.config", "com.flame.config.modules", "plm"}, exclude = {SecurityAutoConfiguration.class})
@EnableTransactionManagement //开启事务的注解
public class XWorxPLMApplication extends SpringBootServletInitializer implements CommandLineRunner {
    public void run(String... args) throws Exception {
        String[] beanNames = BasicConfiguration.getBeanNamesForType(ServletWebServerFactory.class);
        BasicConfiguration.getBean(beanNames[0], ServletWebServerFactory.class);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication plmApplication = new SpringApplication(XWorxPLMApplication.class);
        plmApplication.run(args);
    }

}
