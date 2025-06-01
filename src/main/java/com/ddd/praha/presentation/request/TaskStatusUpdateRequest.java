package com.ddd.praha.presentation.request;

/**
 * 課題ステータス更新リクエスト
 */
public class TaskStatusUpdateRequest {
    private String status;
    
    // デフォルトコンストラクタ（Jackson用）
    public TaskStatusUpdateRequest() {
    }
    
    public TaskStatusUpdateRequest(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}