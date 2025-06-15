package com.ddd.praha.domain.model;

import java.util.Objects;

/**
 * メールアドレスを表す値オブジェクト。
 * 
 * <p>メールアドレスの形式検証を行い、不正な形式では例外をスローする。
 * 一般的なメールアドレス形式（例：user@example.com）に対応している。</p>
 * 
 * <p>バリデーション仕様：</p>
 * <ul>
 *   <li>null値は許可されない</li>
 *   <li>基本的なメールアドレス形式パターンに一致する必要がある</li>
 *   <li>ローカル部：英数字、+、_、.、-を許可</li>
 *   <li>ドメイン部：英数字、.、-を許可、最後に2文字以上の英字</li>
 * </ul>
 * 
 * @param value メールアドレス文字列
 * @throws NullPointerException valueがnullの場合
 * @throws IllegalArgumentException メールアドレス形式が不正な場合
 * 
 */
public record Email(String value) {

  public Email {
    Objects.requireNonNull(value, "メールアドレスは必須です");
    if (!value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
      throw new IllegalArgumentException("不正なメールアドレス形式です: " + value);
    }
  }
}
