package com.ddd.praha.domain;

import java.util.List;

/**
 * メンバー検索結果のDTO
 * ページング情報とメンバーリストを保持する
 */
public class MemberSearchResult {
    private final List<Member> members;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    
    public MemberSearchResult(List<Member> members, int page, int size, long totalElements) {
        this.members = members;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }
    
    public List<Member> getMembers() {
        return members;
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