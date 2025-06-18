package com.ddd.praha._config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ設定クラス
 */
@Configuration
public class RabbitMQConfig {

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
     * RabbitTemplateにJSONコンバーターを設定
     * @param connectionFactory 接続ファクトリー
     * @return 設定済みRabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}