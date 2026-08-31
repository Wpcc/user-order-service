package com.wpcc.userorderservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestMapperConfiguration.class)
class UserOrderServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
