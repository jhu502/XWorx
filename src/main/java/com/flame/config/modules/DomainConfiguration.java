package com.flame.config.modules;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan({"xw.domain.service"})
@EntityScan({"xw.domain"})
@EnableJpaRepositories({"xw.domain.repos"})
public class DomainConfiguration {
}
