package com.ddd.praha.presentation.api;

import com.ddd.praha.domain.entity.Team;

import java.util.List;
import java.util.stream.Collectors;

/**
 * チームレスポンス
 */
public class TeamResponse {
    private String id;
    private String name;
    private List<MemberResponse> members;
    
    // デフォルトコンストラクタ（Jackson用）
    public TeamResponse() {
    }
    
    public TeamResponse(String id, String name, List<MemberResponse> members) {
        this.id = id;
        this.name = name;
        this.members = members;
    }
    
    /**
     * ドメインオブジェクトからレスポンスオブジェクトを作成する
     * @param team ドメインオブジェクト
     * @return レスポンスオブジェクト
     */
    public static TeamResponse from(Team team) {
        List<MemberResponse> memberResponses = team.getMembers().stream()
                .map(MemberResponse::from)
                .collect(Collectors.toList());
                
        return new TeamResponse(
                team.getId().value(),
                team.getName().value(),
                memberResponses
        );
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<MemberResponse> getMembers() {
        return members;
    }
    
    public void setMembers(List<MemberResponse> members) {
        this.members = members;
    }
}