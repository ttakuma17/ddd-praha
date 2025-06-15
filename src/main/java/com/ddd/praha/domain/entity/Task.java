package com.ddd.praha.domain.entity;

import com.ddd.praha.domain.model.*;
import java.util.Objects;

/**
 * プラハチャレンジの学習課題を表すエンティティ。
 * 
 * <p>課題は一意のIDと名前を持つ学習コンテンツの単位である。
 * 参加者は各課題に対して進捗ステータス（未着手、取組中、レビュー待ち、完了）を持つ。</p>
 * 
 * <p>課題の特徴：</p>
 * <ul>
 *   <li>一度作成された課題の名前は変更不可（不変オブジェクト）</li>
 *   <li>課題自体にはステータスは含まれず、参加者との関連で管理される</li>
 *   <li>課題は複数の参加者に同時に割り当て可能</li>
 * </ul>
 */
public class Task {
  private final TaskId id;
  private final TaskName name;

  /**
   * 課題IDを取得する。
   * 
   * @return 課題ID
   */
  public TaskId getId() {
    return id;
  }

  /**
   * 課題名を取得する。
   * 
   * @return 課題名
   */
  public TaskName getName() {
    return name;
  }

  /**
   * 新しい課題を作成する（IDは自動生成）。
   * 
   * @param name 課題名（必須）
   * @throws NullPointerException 課題名がnullの場合
   */
  public Task(TaskName name) {
    this.id = TaskId.generate();
    this.name = Objects.requireNonNull(name, "課題名は必須です");
  }

  /**
   * 既存のIDを指定して課題を復元する（主にリポジトリからの復元用）。
   * 
   * @param id 課題ID（必須）
   * @param name 課題名（必須）
   * @throws NullPointerException いずれかの引数がnullの場合
   */
  public Task(TaskId id, TaskName name) {
    this.id = Objects.requireNonNull(id, "課題IDは必須です");
    this.name = Objects.requireNonNull(name, "課題名は必須です");
  }
}
