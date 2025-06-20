package com.ddd.praha.processor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ProcessorApplication のテスト")
class ProcessorApplicationTests {

    @Test
    @DisplayName("Spring Contextが正常にロードされる")
    void contextLoads() {
        // このテストはSpring Contextが正常にロードされることを確認する
        // テストメソッドが正常に実行されれば、コンテキストのロードが成功している
    }
}