package com.ddd.praha.domain.model;

/**
 * チーム名を表す値オブジェクト。
 * 
 * <p>プラハチャレンジのチーム名を管理し、命名規則を強制する。
 * チーム名は英文字のみ使用可能で、適切な長さ制限を持つ。</p>
 * 
 * <p>バリデーションルール：</p>
 * <ul>
 *   <li>必須入力（null・空文字列不可）</li>
 *   <li>最大20文字以内</li>
 *   <li>英文字（a-z、A-Z）のみ使用可能</li>
 *   <li>数字、記号、日本語は使用不可</li>
 * </ul>
 * 
 * @param value チーム名文字列
 * @throws IllegalArgumentException バリデーションルールに違反する場合
 */
public record TeamName(String value) {

  public TeamName {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("チーム名は必須です");
    }
    if (value.length() > 20) {
      throw new IllegalArgumentException("チーム名は20文字以内にしてください");
    }
    if (!value.matches("^[a-zA-Z]+$")) {
      throw new IllegalArgumentException("チーム名は英文字のみ使用できます");
    }
  }
}
