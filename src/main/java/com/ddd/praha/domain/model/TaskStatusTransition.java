package com.ddd.praha.domain.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 課題ステータス遷移のルールを管理するドメインサービス。
 * 
 * <p>課題進捗の適切なステータス遷移を保証するために、
 * 許可された遷移パターンを定義し、遷移可能性をチェックする。</p>
 * 
 * <p>許可されるステータス遷移：</p>
 * <ul>
 *   <li>未着手 → 取組中</li>
 *   <li>取組中 → レビュー待ち</li>
 *   <li>レビュー待ち → 取組中（再作業）</li>
 *   <li>レビュー待ち → 完了</li>
 * </ul>
 * 
 * <p>完了ステータスからの遷移は一切許可されない。</p>
 */
public class TaskStatusTransition {
  private static final Map<TaskStatus, Set<TaskStatus>> ALLOWED_TRANSITIONS = 
      Map.of(
          TaskStatus.未着手, EnumSet.of(TaskStatus.取組中),
          TaskStatus.取組中, EnumSet.of(TaskStatus.レビュー待ち),
          TaskStatus.レビュー待ち, EnumSet.of(TaskStatus.取組中, TaskStatus.完了)
      );

  /**
   * 指定されたステータス間の遷移が許可されているかを判定する。
   * 
   * <p>遷移ルールに基づいて、現在のステータスから目標ステータスへの
   * 変更が可能かどうかを検証する。</p>
   * 
   * @param from 遷移元のステータス（必須）
   * @param to 遷移先のステータス（必須）
   * @return 遷移が許可されている場合true、そうでなければfalse
   * @throws NullPointerException いずれかの引数がnullの場合
   */
  public boolean canTransit(TaskStatus from, TaskStatus to) {
    Objects.requireNonNull(from, "遷移元ステータスは必須です");
    Objects.requireNonNull(to, "遷移先ステータスは必須です");
    
    Set<TaskStatus> allowedStates = ALLOWED_TRANSITIONS.get(from);
    return allowedStates != null && allowedStates.contains(to);
  }
}
