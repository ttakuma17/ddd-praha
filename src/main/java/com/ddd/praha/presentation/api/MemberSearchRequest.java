package com.ddd.praha.presentation.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 参加者課題検索リクエスト
 * @param taskIds 課題IDのリスト
 * @param statuses ステータスのリスト
 * @param page ページ番号（0から開始）
 */
public record MemberSearchRequest(
    @NotEmpty
    List<String> taskIds,
    @NotEmpty
    List<String> statuses,
    @Min(0)
    int page
) {
}