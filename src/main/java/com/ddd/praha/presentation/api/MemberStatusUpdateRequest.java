package com.ddd.praha.presentation.api;

/**
 * 参加者ステータス更新リクエスト
 */
public class MemberStatusUpdateRequest {
    private String status;
    
    // デフォルトコンストラクタ（Jackson用）
    public MemberStatusUpdateRequest() {
    }
    
    public MemberStatusUpdateRequest(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}