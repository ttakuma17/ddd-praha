package com.ddd.praha.processor;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * ProcessorアプリケーションのTestcontainers設定
 * 
 * RabbitMQコンテナの設定を提供します。
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    RabbitMQContainer rabbitMQContainer() {
        return new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.13-management-alpine"));
    }
}