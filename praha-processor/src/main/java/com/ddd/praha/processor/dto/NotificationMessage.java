package com.ddd.praha.processor.dto;

/**
 * キューから受信する通知メッセージの構造化レコード
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