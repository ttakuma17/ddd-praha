package com.ddd.praha.infrastructure;

import com.ddd.praha.application.repository.NotificationRepository;
import com.ddd.praha.domain.TeamNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * 通知送信の実装
 * 現在はログ出力のみ、将来的にRabbitMQやメール送信に変更予定
 */
@Repository
public class NotificationRepositoryImpl implements NotificationRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationRepositoryImpl.class);

    @Override
    public void sendNotification(TeamNotificationEvent event) {
        switch (event.getType()) {
            case TEAM_SPLIT:
                logger.info("チーム分割通知: {}", event.getMessage());
                break;
            case TEAM_MERGED:
                logger.info("チーム合流通知: {}", event.getMessage());
                break;
            case MONITORING_REQUIRED:
                logger.warn("管理者メール通知: {}", event.getMessage());
                break;
            case MERGE_FAILURE:
                logger.error("管理者通知: {}", event.getMessage());
                sendToQueue(event.getMessage());
                break;
        }
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        // TODO: 実際のメール送信実装
        logger.info("メール送信 - 宛先: {}, 件名: {}, 本文: {}", to, subject, body);
    }

    @Override
    public void sendToQueue(String message) {
        // TODO: RabbitMQのキューにメッセージを格納する実装
        logger.error("RabbitMQキュー送信: {}", message);
    }
}