package com.ddd.praha.processor.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQリスナー設定
 */
@Configuration
@EnableRabbit
public class RabbitMQListenerConfig {

    /**
     * チーム関連の通知用キュー名
     */
    public static final String TEAM_NOTIFICATION_QUEUE = "team.notification.queue";

    /**
     * チーム通知用キューを定義
     * @return キュー設定
     */
    @Bean
    public Queue teamNotificationQueue() {
        return QueueBuilder.durable(TEAM_NOTIFICATION_QUEUE)
                .withArgument("x-message-ttl", 300000) // 5分のTTL
                .build();
    }

    /**
     * JSONメッセージコンバーターを設定
     * @return JSONコンバーター
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitListenerコンテナファクトリーを設定
     * @param connectionFactory 接続ファクトリー
     * @return リスナーコンテナファクトリー
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(5);
        return factory;
    }
}