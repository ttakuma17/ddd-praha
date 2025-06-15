package com.ddd.praha.domain.entity;

import com.ddd.praha.domain.model.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 参加者の課題進捗を管理するエンティティ。
 * 
 * <p>特定の参加者が担当する複数の課題に対する進捗ステータスを管理する。
 * 課題ごとのステータス（未着手、取組中、レビュー待ち、完了）を保持し、
 * ステータス更新時の権限チェックと遷移ルール検証を行う。</p>
 * 
 * <p>主な機能：</p>
 * <ul>
 *   <li>課題ステータスの管理と更新</li>
 *   <li>ステータス更新権限の制御（所有者のみ更新可能）</li>
 *   <li>ステータス遷移ルールの検証</li>
 *   <li>課題別進捗状況の取得</li>
 * </ul>
 * 
 * <p>ステータス遷移ルール：</p>
 * <ul>
 *   <li>未着手 → 取組中</li>
 *   <li>取組中 → レビュー待ち</li>
 *   <li>レビュー待ち → 取組中（再作業）</li>
 *   <li>レビュー待ち → 完了</li>
 * </ul>
 */
public class TaskProgress {
  private final Member owner;
  private final Map<Task, TaskStatus> map;

  /**
   * 課題進捗の所有者（参加者）を取得する。
   * 
   * @return 課題進捗の所有者
   */
  public Member getOwner() {
    return owner;
  }

  /**
   * 参加者と課題リストから新しい課題進捗を作成する。
   * 
   * <p>全ての課題は初期状態「未着手」で設定される。</p>
   * 
   * @param member 課題進捗の所有者（必須）
   * @param tasks 割り当てられる課題リスト（必須）
   * @throws NullPointerException いずれかの引数がnullの場合
   */
  public TaskProgress(Member member, List<Task> tasks) {
    this.owner = Objects.requireNonNull(member, "所有者は必須です");
    Objects.requireNonNull(tasks, "課題リストは必須です");
    this.map = new HashMap<>();
    tasks.forEach(task -> map.put(task, TaskStatus.未着手));
  }

  /**
   * 既存の課題ステータスマップから課題進捗を復元する（主にリポジトリからの復元用）。
   * 
   * @param owner 課題進捗の所有者（必須）
   * @param taskStatuses 課題とステータスのマッピング（必須）
   * @throws NullPointerException いずれかの引数がnullの場合
   */
  public TaskProgress(Member owner, Map<Task, TaskStatus> taskStatuses) {
    this.owner = Objects.requireNonNull(owner, "所有者は必須です");
    this.map = Objects.requireNonNull(taskStatuses, "課題ステータスマップは必須です");
  }

  public void updateTaskStatus(Member operator, Task task, TaskStatus newStatus) {
    if (!owner.equals(operator)) {
      throw new IllegalArgumentException("進捗ステータスを変更できるのは、課題の所有者だけです");
    }
    var transition = new TaskStatusTransition();
    if (!transition.canTransit(map.get(task), newStatus)) {
      throw new IllegalStateException("このステータス変更は許可されていません");
    }
    map.put(task, newStatus);
  }

  public TaskStatus getTaskStatus(Task Task) {
    return map.get(Task);
  }

}
