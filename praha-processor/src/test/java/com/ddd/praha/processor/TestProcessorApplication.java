package com.ddd.praha.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration(proxyBeanMethods = false)
@Import(TestcontainersConfiguration.class)
public class TestProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.from(ProcessorApplication::main)
                .with(TestProcessorApplication.class)
                .run(args);
    }
}