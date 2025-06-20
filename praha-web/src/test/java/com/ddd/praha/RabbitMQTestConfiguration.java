package com.ddd.praha;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * RabbitMQ関連テスト専用のTestcontainers設定
 * 
 * RabbitMQを使用するテストクラスでこの設定をインポートすることで、
 * 独立したRabbitMQコンテナでテストを実行できます。
 */
@TestConfiguration(proxyBeanMethods = false)
public class RabbitMQTestConfiguration {

    @Bean
    @ServiceConnection
    RabbitMQContainer rabbitMQContainer() {
        return new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.13-management-alpine"));
    }
}