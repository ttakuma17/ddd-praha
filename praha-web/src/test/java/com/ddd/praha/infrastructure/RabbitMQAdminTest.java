package com.ddd.praha.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.ddd.praha.TestcontainersConfiguration;
import com.ddd.praha.RabbitMQTestConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RabbitMQ Admin機能のテスト
 */
@SpringBootTest
@Import({TestcontainersConfiguration.class, RabbitMQTestConfiguration.class})
class RabbitMQAdminTest {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Test
    void canCreateAndDeleteQueue() {
        // テスト用キューを作成
        String queueName = "admin-test-queue";
        Queue testQueue = new Queue(queueName, false, false, true);
        
        // キューを宣言
        String actualQueueName = amqpAdmin.declareQueue(testQueue);
        assertEquals(queueName, actualQueueName);
        
        // キューが存在することを確認
        if (amqpAdmin instanceof RabbitAdmin) {
            RabbitAdmin rabbitAdmin = (RabbitAdmin) amqpAdmin;
            QueueInformation queueInfo = rabbitAdmin.getQueueInfo(queueName);
            assertNotNull(queueInfo);
            assertEquals(queueName, queueInfo.getName());
        }
        
        // キューを削除
        boolean deleted = amqpAdmin.deleteQueue(queueName);
        assertTrue(deleted);
    }

    @Test
    void canCreateMultipleQueues() {
        String[] queueNames = {"queue1", "queue2", "queue3"};
        
        // 複数のキューを作成
        for (String queueName : queueNames) {
            Queue queue = new Queue(queueName, false, false, true);
            String actualName = amqpAdmin.declareQueue(queue);
            assertEquals(queueName, actualName);
        }
        
        // すべてのキューを削除
        for (String queueName : queueNames) {
            boolean deleted = amqpAdmin.deleteQueue(queueName);
            assertTrue(deleted);
        }
    }
}