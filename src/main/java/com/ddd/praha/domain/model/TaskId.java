package com.ddd.praha.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * 課題IDを表す値オブジェクト。
 * 
 * <p>課題を一意に識別するためのIDを管理する。
 * UUIDベースの文字列として実装され、システム内で課題の同一性を保証する。</p>
 * 
 * <p>ID生成方式：</p>
 * <ul>
 *   <li>新規作成時は{@link #generate()}メソッドでUUID自動生成</li>
 *   <li>既存IDの復元時は文字列から直接作成</li>
 *   <li>null値や空文字列は許可されない</li>
 * </ul>
 * 
 * @param value 課題ID文字列
 * @throws NullPointerException valueがnullの場合
 * @throws IllegalArgumentException valueが空文字列の場合
 */
public record TaskId(String value) {
  public TaskId {
    Objects.requireNonNull(value, "課題IDは必須です");
    if (value.isBlank()) {
      throw new IllegalArgumentException("課題IDは空文字列にできません");
    }
  }

  /**
   * 新しい課題IDを自動生成する。
   * 
   * <p>UUID.randomUUID()を使用して一意のIDを生成する。</p>
   * 
   * @return 新しい課題ID
   */
  public static TaskId generate() {
    return new TaskId(UUID.randomUUID().toString());
  }

}
