package com.ddd.praha.presentation.request;

/**
 * 参加者作成リクエスト
 */
public class MemberCreateRequest {
    private String name;
    private String email;
    private String status;
    
    // デフォルトコンストラクタ（Jackson用）
    public MemberCreateRequest() {
    }
    
    public MemberCreateRequest(String name, String email, String status) {
        this.name = name;
        this.email = email;
        this.status = status;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}