package com.ddd.praha.domain.model;

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
