package com.flame.config.modules;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan({"plm.part", "plm.cad", "plm.doc", "plm.dynamic"})
@ComponentScan({"plm.dynamic.service", "plm.part.service", "plm.cad.service", "plm.doc.service"})
@EnableJpaRepositories({"plm.part.repos", "plm.cad.repos", "plm.doc.repos", "plm.dynamic.repos"})
public class PLMConfiguration {
}
