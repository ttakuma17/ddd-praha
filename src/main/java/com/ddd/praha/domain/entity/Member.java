package com.ddd.praha.domain.entity;

import com.ddd.praha.domain.model.*;
import java.util.Objects;

/**
 * メンバーエンティティ
 */
public class Member {
  private final MemberId id;
  private final MemberName name;
  private final Email email;
  private EnrollmentStatus status;

  public Member(MemberName name, Email email, EnrollmentStatus status) {
    this.id = MemberId.generate();
    this.name = Objects.requireNonNull(name, "名前は必須です");
    this.email = Objects.requireNonNull(email, "メールアドレスは必須です");
    this.status = Objects.requireNonNull(status, "受講ステータスは必須です");
  }

  public Member(MemberId id, MemberName name, Email email, EnrollmentStatus status) {
    this.id = Objects.requireNonNull(id, "メンバーIDは必須です");
    this.name = Objects.requireNonNull(name, "名前は必須です");
    this.email = Objects.requireNonNull(email, "メールアドレスは必須です");
    this.status = Objects.requireNonNull(status, "受講ステータスは必須です");
  }

  public MemberId getId() {
    return id;
  }

  public MemberName getName() {
    return name;
  }

  public Email getEmail() {
    return email;
  }

  public EnrollmentStatus getStatus() {
    return status;
  }

  public boolean canJoin() {
    return status == EnrollmentStatus.在籍中;
  }

  public void updateEnrollmentStatus(EnrollmentStatus newStatus) {
    var transition = new EnrollmentStatusTransition();
    if (!transition.canTransit(this.status, newStatus)) {
      throw new IllegalStateException("このステータス変更は許可されていません");
    }
    this.status = newStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Member member = (Member) o;
    // idだけで比較する場合（エンティティの同一性）
    return Objects.equals(id, member.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }


}
