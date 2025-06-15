package com.ddd.praha.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * 参加者IDを表す値オブジェクト。
 * 
 * <p>参加者を一意に識別するためのIDを管理する。
 * UUIDベースの文字列として実装され、システム内で参加者の同一性を保証する。</p>
 * 
 * <p>ID生成方式：</p>
 * <ul>
 *   <li>新規作成時は{@link #generate()}メソッドでUUID自動生成</li>
 *   <li>既存IDの復元時は文字列から直接作成</li>
 *   <li>null値や空文字列は許可されない</li>
 * </ul>
 * 
 * @param value 参加者ID文字列
 * @throws NullPointerException valueがnullの場合
 * @throws IllegalArgumentException valueが空文字列の場合
 */
public record MemberId(String value) {
  public MemberId {
    Objects.requireNonNull(value, "参加者IDは必須です");
    if (value.isBlank()) {
      throw new IllegalArgumentException("参加者IDは空文字列にできません");
    }
  }

  public static MemberId generate() {
    return new MemberId(UUID.randomUUID().toString());
  }
}
