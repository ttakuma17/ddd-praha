package com.ddd.praha.presentation.api;

/**
 * カーソルベースページネーションのリクエスト
 */
public class CursorPageRequest {
    
    /**
     * 課題IDの配列（複数指定可能）
     */
    private String[] taskIds;
    
    /**
     * ステータスの配列（複数指定可能）
     */
    private String[] statuses;
    
    /**
     * カーソル（前回のページで取得した最後の要素のID）
     * nullまたは空文字列の場合は最初のページ
     */
    private String cursor;
    
    /**
     * 取得件数の上限（デフォルト：10、最大：100）
     */
    private int limit = 10;
    
    public CursorPageRequest() {
    }
    
    public CursorPageRequest(String[] taskIds, String[] statuses, String cursor, int limit) {
        this.taskIds = taskIds;
        this.statuses = statuses;
        this.cursor = cursor;
        this.limit = Math.min(limit, 100); // 最大100件まで
    }
    
    public String[] getTaskIds() {
        return taskIds;
    }
    
    public void setTaskIds(String[] taskIds) {
        this.taskIds = taskIds;
    }
    
    public String[] getStatuses() {
        return statuses;
    }
    
    public void setStatuses(String[] statuses) {
        this.statuses = statuses;
    }
    
    public String getCursor() {
        return cursor;
    }
    
    public void setCursor(String cursor) {
        this.cursor = cursor;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        this.limit = Math.min(limit, 100);
    }
    
    /**
     * 最初のページかどうかを判定
     */
    public boolean isFirstPage() {
        return cursor == null || cursor.trim().isEmpty();
    }
}