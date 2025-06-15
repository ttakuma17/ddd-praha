package com.ddd.praha.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * チームIDを表す値オブジェクト。
 * 
 * <p>チームを一意に識別するためのIDを管理する。
 * UUIDベースの文字列として実装され、システム内でチームの同一性を保証する。</p>
 * 
 * <p>ID生成方式：</p>
 * <ul>
 *   <li>新規作成時は{@link #generate()}メソッドでUUID自動生成</li>
 *   <li>既存IDの復元時は文字列から直接作成</li>
 *   <li>null値や空文字列は許可されない</li>
 * </ul>
 * 
 * @param value チームID文字列
 * @throws NullPointerException valueがnullの場合
 * @throws IllegalArgumentException valueが空文字列の場合
 */
public record TeamId(String value) {
  public TeamId {
    Objects.requireNonNull(value, "チームIDは必須です");
    if (value.isBlank()) {
      throw new IllegalArgumentException("チームIDは空文字列にできません");
    }
  }

  /**
   * 新しいチームIDを自動生成する。
   * 
   * <p>UUID.randomUUID()を使用して一意のIDを生成する。</p>
   * 
   * @return 新しいチームID
   */
  public static TeamId generate() {
    return new TeamId(UUID.randomUUID().toString());
  }
}
