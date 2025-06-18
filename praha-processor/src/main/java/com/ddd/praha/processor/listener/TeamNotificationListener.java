package com.ddd.praha.processor.listener;

import com.ddd.praha.processor.dto.NotificationMessage;
import com.ddd.praha.processor.config.RabbitMQListenerConfig;
import com.ddd.praha.processor.service.NotificationProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * チーム通知メッセージリスナー
 */
@Component
public class TeamNotificationListener {
    
    private static final Logger logger = LoggerFactory.getLogger(TeamNotificationListener.class);
    private final NotificationProcessorService processorService;
    
    public TeamNotificationListener(NotificationProcessorService processorService) {
        this.processorService = processorService;
    }
    
    /**
     * チーム通知キューからメッセージを受信して処理
     * @param message 受信した通知メッセージ
     */
    @RabbitListener(queues = RabbitMQListenerConfig.TEAM_NOTIFICATION_QUEUE)
    public void handleTeamNotification(NotificationMessage message) {
        logger.info("チーム通知メッセージを受信しました: Type={}, Timestamp={}", 
            message.type(), message.timestamp());
        
        try {
            processorService.processNotification(message);
            logger.info("メッセージの処理が完了しました: Type={}", message.type());
        } catch (Exception e) {
            logger.error("メッセージ処理中にエラーが発生しました: Type={}", message.type(), e);
            // エラーハンドリング: 必要に応じてDLQへの転送やリトライ処理を実装
            throw e; // RabbitMQに処理失敗を通知
        }
    }
}