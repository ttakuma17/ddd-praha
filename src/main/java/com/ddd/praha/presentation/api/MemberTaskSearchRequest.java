package com.ddd.praha.presentation.api;

/**
 * 参加者課題検索リクエスト
 */
public class MemberTaskSearchRequest {
    
    /**
     * 課題IDの配列（複数指定可能）
     */
    private String[] taskIds;
    
    /**
     * ステータスの配列（複数指定可能）
     */
    private String[] statuses;
    
    /**
     * ページ番号（0から開始、デフォルト：0）
     */
    private int page = 0;
    
    public MemberTaskSearchRequest() {
    }
    
    public MemberTaskSearchRequest(String[] taskIds, String[] statuses, int page) {
        this.taskIds = taskIds;
        this.statuses = statuses;
        this.page = page;
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
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
}