package com.ddd.praha.infrastructure;


import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.model.Email;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.model.MemberName;

/**
 * メンバーのSQLマッピングレコード
 */
public record MemberRecord(
    String id,
    String name,
    String email,
    String status
) {

    /**
     * ドメインのMemberオブジェクトに変換する
     * @return Member
     */
    public Member toMember() {
        return new Member(
            new MemberId(id),
            new MemberName(name),
            new Email(email),
            EnrollmentStatus.valueOf(status)
        );
    }
}
