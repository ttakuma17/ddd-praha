package com.ddd.praha.domain;

public record Email(String value) {

  public Email {
    if (!value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
      throw new IllegalArgumentException("不正なメールアドレス形式です: " + value);
    }
  }
}
