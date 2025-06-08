package com.ddd.praha.infrastructure;

import com.ddd.praha.application.repository.NotificationRepository;
import com.ddd.praha.domain.TeamNotificationEvent;
import com.ddd.praha.infrastructure.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Repository;

/**
 * 通知送信の実装
 * RabbitMQキューへのメッセージ送信とログ出力を実装
 */
@Repository
public class NotificationRepositoryImpl implements NotificationRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationRepositoryImpl.class);
    private final RabbitTemplate rabbitTemplate;
    
    public NotificationRepositoryImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

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
                sendNotificationEventToQueue(event);
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
        try {
            // RabbitMQキューにメッセージを送信
            rabbitTemplate.convertAndSend(RabbitMQConfig.TEAM_NOTIFICATION_QUEUE, message);
            logger.info("RabbitMQキューへメッセージを送信しました: {}", message);
        } catch (Exception e) {
            logger.error("RabbitMQキューへのメッセージ送信に失敗しました: {}", message, e);
            // フォールバックとしてログに記録
            logger.error("フォールバック - 通知メッセージ: {}", message);
        }
    }
    
    /**
     * TeamNotificationEventの構造化された情報をキューに送信
     * @param event 通知イベント
     */
    private void sendNotificationEventToQueue(TeamNotificationEvent event) {
        try {
            // 構造化された通知情報を作成
            NotificationMessage notificationMessage = new NotificationMessage(
                event.getType().name(),
                event.getMessage(),
                event.getTeam() != null ? event.getTeam().getId().value() : null,
                event.getTeam() != null ? event.getTeam().getName().value() : null,
                event.getMember() != null ? event.getMember().getId().value() : null,
                event.getMember() != null ? event.getMember().getName().value() : null,
                System.currentTimeMillis()
            );
            
            rabbitTemplate.convertAndSend(RabbitMQConfig.TEAM_NOTIFICATION_QUEUE, notificationMessage);
            logger.info("構造化された通知イベントをRabbitMQキューに送信しました: {}", event.getType());
        } catch (Exception e) {
            logger.error("構造化された通知イベントのRabbitMQキュー送信に失敗しました", e);
            // フォールバックとして文字列メッセージを送信
            sendToQueue(event.getMessage());
        }
    }
    
    /**
     * キューに送信する通知メッセージの構造化レコード
     */
    public record NotificationMessage(
        String type,
        String message,
        String teamId,
        String teamName,
        String memberId,
        String memberName,
        long timestamp
    ) {}
}