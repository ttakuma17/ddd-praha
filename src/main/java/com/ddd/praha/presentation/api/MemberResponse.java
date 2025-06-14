package com.ddd.praha.presentation.api;

import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.model.Email;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.model.MemberName;
import java.util.Objects;
import org.springframework.lang.NonNull;

/**
 * 参加者レスポンス
 */
public record MemberResponse(String id, String name, String email, String status) {
  /**
   * ドメインオブジェクトからレスポンスオブジェクトを作成する
   *
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
   *
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
}
