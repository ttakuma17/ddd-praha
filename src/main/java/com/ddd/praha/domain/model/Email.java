package com.ddd.praha.domain.model;

public record Email(String value) {

  public Email {
    if (!value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
      throw new IllegalArgumentException("不正なメールアドレス形式です: " + value);
    }
  }
}
