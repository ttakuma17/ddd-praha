package com.ddd.praha.domain.entity;

import com.ddd.praha.domain.model.*;
import java.util.Objects;

/**
 * プラハチャレンジの参加者を表すエンティティ。
 * 
 * <p>参加者は一意のIDを持ち、名前、メールアドレス、在籍ステータスの情報を管理する。
 * チームへの参加資格や在籍ステータスの遷移に関するビジネスルールを実装している。</p>
 * 
 * <p>在籍ステータスは以下の3つの状態を持つ：</p>
 * <ul>
 *   <li>在籍中 - アクティブな参加者でチームに参加可能</li>
 *   <li>休会中 - 一時的に活動を停止している参加者</li>
 *   <li>退会済 - プログラムを終了した参加者（復帰可能）</li>
 * </ul>
 * 
 */
public class Member {
  private final MemberId id;
  private final MemberName name;
  private final Email email;
  private EnrollmentStatus status;

  /**
   * 新しい参加者を作成する（IDは自動生成）。
   * 
   * @param name 参加者の名前（必須）
   * @param email 参加者のメールアドレス（必須、形式検証済み）
   * @param status 初期在籍ステータス（必須）
   * @throws NullPointerException いずれかの引数がnullの場合
   */
  public Member(MemberName name, Email email, EnrollmentStatus status) {
    this.id = MemberId.generate();
    this.name = Objects.requireNonNull(name, "名前は必須です");
    this.email = Objects.requireNonNull(email, "メールアドレスは必須です");
    this.status = Objects.requireNonNull(status, "受講ステータスは必須です");
  }

  /**
   * 既存のIDを指定して参加者を復元する（主にリポジトリからの復元用）。
   * 
   * @param id 参加者ID（必須）
   * @param name 参加者の名前（必須）
   * @param email 参加者のメールアドレス（必須、形式検証済み）
   * @param status 在籍ステータス（必須）
   * @throws NullPointerException いずれかの引数がnullの場合
   */
  public Member(MemberId id, MemberName name, Email email, EnrollmentStatus status) {
    this.id = Objects.requireNonNull(id, "メンバーIDは必須です");
    this.name = Objects.requireNonNull(name, "名前は必須です");
    this.email = Objects.requireNonNull(email, "メールアドレスは必須です");
    this.status = Objects.requireNonNull(status, "受講ステータスは必須です");
  }

  /**
   * 参加者IDを取得する。
   * 
   * @return 参加者ID
   */
  public MemberId getId() {
    return id;
  }

  /**
   * 参加者の名前を取得する。
   * 
   * @return 参加者名
   */
  public MemberName getName() {
    return name;
  }

  /**
   * 参加者のメールアドレスを取得する。
   * 
   * @return メールアドレス
   */
  public Email getEmail() {
    return email;
  }

  /**
   * 現在の在籍ステータスを取得する。
   * 
   * @return 在籍ステータス
   */
  public EnrollmentStatus getStatus() {
    return status;
  }

  /**
   * この参加者がチームに参加可能かどうかを判定する。
   * 
   * <p>チームに参加するためには、在籍ステータスが「在籍中」である必要がある。
   * 休会中や退会済みの参加者はチームに参加できない。</p>
   * 
   * @return チーム参加可能な場合はtrue、そうでなければfalse
   */
  public boolean canJoin() {
    return status == EnrollmentStatus.在籍中;
  }

  /**
   * 在籍ステータスを更新する。
   * 
   * <p>ステータスの遷移は{@link EnrollmentStatusTransition}で定義されたルールに従う必要がある。
   * 不正な遷移を試行した場合は例外がスローされる。</p>
   * 
   * <p>有効な遷移パターン：</p>
   * <ul>
   *   <li>在籍中 → 休会中</li>
   *   <li>在籍中 → 退会済</li>
   *   <li>休会中 → 在籍中</li>
   *   <li>休会中 → 退会済</li>
   *   <li>退会済 → 在籍中（復帰）</li>
   * </ul>
   * 
   * @param newStatus 新しい在籍ステータス（必須）
   * @throws IllegalStateException 無効なステータス遷移を試行した場合
   */
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
