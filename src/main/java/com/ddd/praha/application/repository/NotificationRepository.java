package com.ddd.praha.application.repository;

import com.ddd.praha.domain.TeamNotificationEvent;

/**
 * 通知処理のインターフェース
 * 実装はインフラストラクチャ層で行う
 */
public interface NotificationRepository {
    
    /**
     * チーム関連のイベントに基づいて通知を送信する
     * @param event 通知イベント
     */
    void sendNotification(TeamNotificationEvent event);
    
    /**
     * メール通知を送信する
     * @param to 宛先
     * @param subject 件名
     * @param body 本文
     */
    void sendEmail(String to, String subject, String body);
    
    /**
     * メッセージキューに通知を送信する
     * @param message メッセージ
     */
    void sendToQueue(String message);
}