//package com.ddd.praha;
//
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
//import org.springframework.context.annotation.Bean;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.utility.DockerImageName;
//
//@TestConfiguration(proxyBeanMethods = false)
//public class TestContainerLocalConfiguration {
//
//  @Bean
//  @ServiceConnection
//  PostgreSQLContainer<?> postgresContainer() {
//    PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine3.21"));
//    container.withEnv("TZ","Asia/Tokyo");
//    container.withReuse(true);
//    container.start();
//
//    return container;
//  }
//
//}
