package com.ddd.praha.processor.team;

/**
 * キューから受信する通知メッセージの構造化レコード
 */
public record TeamNotificationMessage(
    String type,
    String message,
    String teamId,
    String teamName,
    String memberId,
    String memberName,
    long timestamp
) {}