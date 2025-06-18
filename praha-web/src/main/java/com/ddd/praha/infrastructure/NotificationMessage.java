package com.ddd.praha.infrastructure;

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