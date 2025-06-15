package com.ddd.praha.domain.model;

import java.util.Objects;

public record Email(String value) {

  public Email {
    Objects.requireNonNull(value, "メールアドレスは必須です");
    if (!value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
      throw new IllegalArgumentException("不正なメールアドレス形式です: " + value);
    }
  }
}
