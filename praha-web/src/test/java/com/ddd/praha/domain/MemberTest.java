package com.ddd.praha.domain;

import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.model.Email;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

  @Nested
  @DisplayName("メンバー作成のテスト")
  class CreateMemberTest {

    @Test
    @DisplayName("有効な値でメンバーを作成できる")
    void 有効な値でメンバーを作成できる() {
      MemberName name = new MemberName("山田太郎");
      Email email = new Email("yamada@example.com");
      EnrollmentStatus status = EnrollmentStatus.在籍中;

      Member member = new Member(name, email, status);

      assertNotNull(member);
    }

    @Test
    @DisplayName("名前がnullの場合は例外がスローされる")
    void 名前がnullの場合は例外がスローされる() {
      Email email = new Email("yamada@example.com");
      EnrollmentStatus status = EnrollmentStatus.在籍中;

      Exception exception = assertThrows(NullPointerException.class, () -> {
        new Member(null, email, status);
      });
      assertEquals("名前は必須です", exception.getMessage());
    }

    @Test
    @DisplayName("メールアドレスがnullの場合は例外がスローされる")
    void メールアドレスがnullの場合は例外がスローされる() {
      MemberName name = new MemberName("山田太郎");
      EnrollmentStatus status = EnrollmentStatus.在籍中;

      Exception exception = assertThrows(NullPointerException.class, () -> {
        new Member(name, null, status);
      });
      assertEquals("メールアドレスは必須です", exception.getMessage());
    }

    @Test
    @DisplayName("受講ステータスがnullの場合は例外がスローされる")
    void 受講ステータスがnullの場合は例外がスローされる() {
      MemberName name = new MemberName("山田太郎");
      Email email = new Email("yamada@example.com");

      Exception exception = assertThrows(NullPointerException.class, () -> {
        new Member(name, email, null);
      });
      assertEquals("受講ステータスは必須です", exception.getMessage());
    }
  }

  @Nested
  @DisplayName("チーム参加資格の確認テスト")
  class CanJoinTest {

    @Test
    @DisplayName("在籍中のメンバーはチームに参加できる")
    void 在籍中のメンバーはチームに参加できる() {
      Member member = new Member(
          new MemberName("山田太郎"),
          new Email("yamada@example.com"),
          EnrollmentStatus.在籍中
      );

      assertTrue(member.canJoin());
    }

    @Test
    @DisplayName("休会中のメンバーはチームに参加できない")
    void 休会中のメンバーはチームに参加できない() {
      Member member = new Member(
          new MemberName("山田太郎"),
          new Email("yamada@example.com"),
          EnrollmentStatus.休会中
      );

      assertFalse(member.canJoin());
    }

    @Test
    @DisplayName("退会済みのメンバーはチームに参加できない")
    void 退会済みのメンバーはチームに参加できない() {
      Member member = new Member(
          new MemberName("山田太郎"),
          new Email("yamada@example.com"),
          EnrollmentStatus.退会済
      );

      assertFalse(member.canJoin());
    }
  }

  @Nested
  @DisplayName("受講ステータス更新のテスト")
  class UpdateEnrollmentStatusTest {

    @Test
    @DisplayName("有効なステータス遷移の場合は更新できる")
    void 有効なステータス遷移の場合は更新できる() {
      Member member = new Member(
          new MemberName("山田太郎"),
          new Email("yamada@example.com"),
          EnrollmentStatus.在籍中
      );

      // 実際の遷移ルールはEnrollmentStatusTransitionに定義されているはずなので
      // 以下のテストはその実装に依存します
      member.updateEnrollmentStatus(EnrollmentStatus.休会中);
      // 検証用のgetterがないため、canJoinメソッドの結果で間接的に検証
      assertFalse(member.canJoin());
    }

    @Test
    @DisplayName("無効なステータス遷移の場合は例外がスローされる")
    void 無効なステータス遷移の場合は例外がスローされる() {
      // 不正な遷移パターンが判断できないため、例外が出ることだけを確認
      // 実際のテストではEnrollmentStatusTransitionの実装を確認して適切な
      // 無効な遷移パターンを選択する必要があります
      Member member = new Member(
          new MemberName("山田太郎"),
          new Email("yamada@example.com"),
          EnrollmentStatus.退会済
      );

      assertThrows(IllegalStateException.class, () -> {
        member.updateEnrollmentStatus(EnrollmentStatus.退会済);
      });
    }
  }
}