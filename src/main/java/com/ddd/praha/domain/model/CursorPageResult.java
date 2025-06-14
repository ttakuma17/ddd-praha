package com.ddd.praha.domain.model;

import java.util.List;

/**
 * カーソルベースページネーションの結果
 */
public class CursorPageResult<T> {
    private final List<T> content;
    private final String nextCursor;
    private final boolean hasNextPage;
    private final int pageSize;
    private final Long totalElements; // 概算値（オプション）
    
    public CursorPageResult(List<T> content, String nextCursor, boolean hasNextPage, int pageSize) {
        this(content, nextCursor, hasNextPage, pageSize, null);
    }
    
    public CursorPageResult(List<T> content, String nextCursor, boolean hasNextPage, int pageSize, Long totalElements) {
        this.content = content;
        this.nextCursor = nextCursor;
        this.hasNextPage = hasNextPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
    }
    
    public List<T> getContent() {
        return content;
    }
    
    public String getNextCursor() {
        return nextCursor;
    }
    
    public boolean hasNextPage() {
        return hasNextPage;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public Long getTotalElements() {
        return totalElements;
    }
    
    public boolean isEmpty() {
        return content.isEmpty();
    }
    
    public int getNumberOfElements() {
        return content.size();
    }
}