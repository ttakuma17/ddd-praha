package com.ddd.praha.domain.model;

import java.util.Objects;

/**
 * 課題名を表す値オブジェクト。
 * 
 * <p>プラハチャレンジの学習課題の名前を管理し、適切な入力値検証を行う。
 * 課題名は学習者にとって理解しやすく、適切な長さである必要がある。</p>
 * 
 * <p>バリデーションルール：</p>
 * <ul>
 *   <li>必須入力（null・空文字列不可）</li>
 *   <li>最大100文字以内</li>
 *   <li>文字種制限なし（日本語、英語、記号すべて使用可能）</li>
 * </ul>
 * 
 * @param value 課題名文字列
 * @throws NullPointerException valueがnullの場合
 * @throws IllegalArgumentException valueが空文字列または100文字超過の場合
 */
public record TaskName(String value) {
  public TaskName {
    Objects.requireNonNull(value, "課題名は必須です");
    if (value.isBlank()) {
      throw new IllegalArgumentException("課題名は空文字列にできません");
    }
    if (value.length() > 100) {
      throw new IllegalArgumentException("課題名は100文字以内にしてください");
    }
  }
}
