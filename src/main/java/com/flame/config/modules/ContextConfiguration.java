package com.flame.config.modules;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan({"xw.context.service"})
@EntityScan({"xw.context.entity"})
@EnableJpaRepositories({"xw.context.repos"})
public class ContextConfiguration {
}
