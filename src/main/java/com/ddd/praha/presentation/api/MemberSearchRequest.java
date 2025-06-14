package com.ddd.praha.presentation.api;

import java.util.List;

/**
 * 参加者課題検索リクエスト
 */
public record MemberSearchRequest(List<String> taskIds, List<String> statuses){
}