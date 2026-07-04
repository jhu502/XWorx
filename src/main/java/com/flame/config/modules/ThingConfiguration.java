package com.flame.config.modules;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan({"com.thing.runtime", "com.thing.service", "com.thing.controller"})
@EntityScan({"com.thing.entity"})
@EnableJpaRepositories({"com.thing.repos"})
public class ThingConfiguration {
}
