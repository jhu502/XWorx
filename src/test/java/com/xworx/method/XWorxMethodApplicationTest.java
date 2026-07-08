package com.xworx.method;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("Test")
@EntityScan(basePackages = { "com.flame" })
class XWorxMethodApplicationTest {
	protected Logger logger = LoggerFactory.getLogger(XWorxMethodApplicationTest.class);

	@Test
	void test() {

	}
}
