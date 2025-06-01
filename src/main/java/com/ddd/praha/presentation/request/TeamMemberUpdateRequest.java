package com.ddd.praha.presentation.request;

import java.util.List;

/**
 * チームメンバー更新リクエスト
 */
public class TeamMemberUpdateRequest {
    private List<String> memberIds;
    
    // デフォルトコンストラクタ（Jackson用）
    public TeamMemberUpdateRequest() {
    }
    
    public TeamMemberUpdateRequest(List<String> memberIds) {
        this.memberIds = memberIds;
    }
    
    public List<String> getMemberIds() {
        return memberIds;
    }
    
    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }
}