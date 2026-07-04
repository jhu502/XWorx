package com.flame.config.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

/**
 * 配置Minio客户端的连接
 * @author hujin
 */
@Configuration
public class XFlameStorageConfigurer {
	private static final Logger logger = LoggerFactory.getLogger(XFlameStorageConfigurer.class);
	@Value("${minio.endpoint}")
	private String endpoint;
	@Value("${minio.accessKey}")
	private String accessKey;
	@Value("${minio.secretKey}")
	private String secretKey;

	/**
	 * 注入minio 客户端
	 * @return
	 */
	@Bean
	public MinioClient minioClient() {
		logger.info("EndPoint:" + endpoint);
		logger.info("AccessKey:" + accessKey);
		logger.info("SecretKey:" + secretKey);
		return MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
	}
}
