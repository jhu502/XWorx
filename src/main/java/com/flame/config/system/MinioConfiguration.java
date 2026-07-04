package com.flame.config.system;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({ "com.flame.minio" })
public class MinioConfiguration {

}
