package com.ddd.praha.presentation.api;

import com.ddd.praha.domain.MemberSearchResult;
import com.ddd.praha.domain.Member;

import java.util.List;
import java.util.stream.Collectors;

public class MemberTaskSearchResponse<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    
    public MemberTaskSearchResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }
    
    /**
     * MemberSearchResultからMemberTaskSearchResponseに変換する
     */
    public static MemberTaskSearchResponse<MemberResponse> fromMemberSearchResult(MemberSearchResult searchResult) {
        List<MemberResponse> memberResponses = searchResult.getMembers().stream()
            .map(MemberResponse::from)
            .collect(Collectors.toList());
        
        return new MemberTaskSearchResponse<>(
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
        if (!(content instanceof List<?>)) {
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