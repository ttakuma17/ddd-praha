package com.ddd.praha;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PrahaApplicationTests {

	@Test
	void コンテキストが正常に読み込まれる() {
	}

}
