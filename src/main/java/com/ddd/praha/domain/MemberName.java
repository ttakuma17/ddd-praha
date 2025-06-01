package com.ddd.praha.domain;

import java.util.Objects;

public record MemberName(String value) {
  public MemberName {
    Objects.requireNonNull(value, "名前は必須です");
    if (value.isBlank()) {
      throw new IllegalArgumentException("名前は空文字列にできません");
    }
  }
}
