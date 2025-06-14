package com.ddd.praha.presentation.api;

import com.ddd.praha.domain.model.MemberSearchResult;
import com.ddd.praha.domain.entity.Member;

import java.util.List;
import java.util.stream.Collectors;

public class MemberSearchResponse<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    
    public MemberSearchResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }
    
    /**
     * MemberSearchResultからMemberTaskSearchResponseに変換する
     */
    public static MemberSearchResponse<MemberResponse> from(MemberSearchResult searchResult) {
        List<MemberResponse> memberResponses = searchResult.getMembers().stream()
            .map(MemberResponse::from)
            .collect(Collectors.toList());
        
        return new MemberSearchResponse<>(
            memberResponses,
            searchResult.getPage(),
            searchResult.getSize(),
            searchResult.getTotalElements()
        );
    }
    
    /**
     * application層で使用するためのMemberSearchResultに変換する
     */
    public MemberSearchResult toMemberSearchResult() {
        if (content == null) {
            throw new IllegalStateException("コンテンツがMemberResponseのリストではありません");
        }
        
        @SuppressWarnings("unchecked")
        List<MemberResponse> memberResponses = (List<MemberResponse>) content;
        
        List<Member> members = memberResponses.stream()
            .map(MemberResponse::toMember)
            .collect(Collectors.toList());
        
        return new MemberSearchResult(members, page, size, totalElements);
    }
    
    public List<T> getContent() {
        return content;
    }
    
    public int getPage() {
        return page;
    }
    
    public int getSize() {
        return size;
    }
    
    public long getTotalElements() {
        return totalElements;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public boolean isFirst() {
        return page == 0;
    }
    
    public boolean isLast() {
        return page >= totalPages - 1;
    }
}