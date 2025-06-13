package com.ddd.praha.presentation.api;

import com.ddd.praha.domain.*;
import java.util.Objects;
import org.springframework.lang.NonNull;

/**
 * 参加者レスポンス
 */
public class MemberResponse {
    private String id;
    private String name;
    private String email;
    private String status;
    
    // デフォルトコンストラクタ（Jackson用）
    public MemberResponse() {
    }
    
    public MemberResponse(String id, String name, String email, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
    }
    
    /**
     * ドメインオブジェクトからレスポンスオブジェクトを作成する
     * @param member ドメインオブジェクト
     * @return レスポンスオブジェクト
     */
    public static MemberResponse from(@NonNull Member member) {
        Objects.requireNonNull(member, "Member must not be null");
        return new MemberResponse(
                member.getId().value(),
                member.getName().value(),
                member.getEmail().value(),
                member.getStatus().name()
        );
    }
    
    /**
     * レスポンスオブジェクトからドメインオブジェクトに変換する
     * @return ドメインオブジェクト
     */
    public Member toMember() {
        return new Member(
                new MemberId(id),
                new MemberName(name),
                new Email(email),
                EnrollmentStatus.valueOf(status)
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