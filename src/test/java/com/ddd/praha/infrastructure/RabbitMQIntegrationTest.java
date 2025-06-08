package com.ddd.praha.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.ddd.praha.TestcontainersConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RabbitMQ統合テスト
 * TestcontainersでRabbitMQコンテナを起動してテストを実行
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource(properties = {
    "spring.rabbitmq.test.enabled=true"
})
class RabbitMQIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
        // RabbitTemplateが正常にDIされることを確認
        assertNotNull(rabbitTemplate);
    }

    @Test
    void canSendAndReceiveMessage() {
        // テスト用キューの名前
        String queueName = "test-queue";
        String testMessage = "Hello RabbitMQ from Testcontainers!";

        // キューを宣言（存在しない場合は作成）
        Queue testQueue = new Queue(queueName, false, false, true);
        rabbitTemplate.execute(channel -> {
            channel.queueDeclare(queueName, false, false, true, null);
            return null;
        });

        // メッセージを送信
        rabbitTemplate.convertAndSend(queueName, testMessage);

        // メッセージを受信
        String receivedMessage = (String) rabbitTemplate.receiveAndConvert(queueName);

        // メッセージが正しく送受信されることを確認
        assertEquals(testMessage, receivedMessage);
    }

    @Test
    void canSendMultipleMessages() {
        String queueName = "multi-test-queue";
        
        // キューを宣言
        rabbitTemplate.execute(channel -> {
            channel.queueDeclare(queueName, false, false, true, null);
            return null;
        });

        // 複数のメッセージを送信
        for (int i = 1; i <= 3; i++) {
            String message = "Test message " + i;
            rabbitTemplate.convertAndSend(queueName, message);
        }

        // メッセージを順次受信して確認
        for (int i = 1; i <= 3; i++) {
            String expectedMessage = "Test message " + i;
            String receivedMessage = (String) rabbitTemplate.receiveAndConvert(queueName);
            assertEquals(expectedMessage, receivedMessage);
        }
    }
}