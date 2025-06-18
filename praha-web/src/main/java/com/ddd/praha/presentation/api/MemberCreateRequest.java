package com.ddd.praha.presentation.api;

/**
 * 参加者作成リクエスト
 */
public record MemberCreateRequest (String name, String email, String status) {
}