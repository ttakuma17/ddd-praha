package com.ddd.praha.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestProcessorApplication {

    // RabbitMQはdocker-composeで管理するため、Testcontainersの設定を削除

    public static void main(String[] args) {
        SpringApplication.from(ProcessorApplication::main)
                .with(TestProcessorApplication.class)
                .run(args);
    }
}