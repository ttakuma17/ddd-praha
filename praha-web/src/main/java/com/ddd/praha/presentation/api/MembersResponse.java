package com.ddd.praha.presentation.api;

import com.ddd.praha.domain.model.MemberSearchResult;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参加者検索レスポンス
 * @param members 参加者リスト
 * @param page 現在のページ番号
 * @param size ページサイズ
 * @param totalElements 全要素数
 * @param totalPages 全ページ数
 * @param isFirst 最初のページかどうか
 * @param isLast 最後のページかどうか
 */
public record MembersResponse(
    List<MemberResponse> members,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean isFirst,
    boolean isLast
) {
    /**
     * MemberSearchResultからMembersResponseを生成
     */
    public static MembersResponse from(MemberSearchResult result) {
        List<MemberResponse> memberResponses = result.getMembers().stream()
            .map(MemberResponse::from)
            .collect(Collectors.toList());
            
        return new MembersResponse(
            memberResponses,
            result.getPage(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.isFirst(),
            result.isLast()
        );
    }
}
