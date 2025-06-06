package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberName;
import com.ddd.praha.domain.Email;
import com.ddd.praha.domain.EnrollmentStatus;

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
