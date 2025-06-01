package com.ddd.praha.domain;

import java.util.Objects;

public record TaskName(String value) {
  public TaskName {
    Objects.requireNonNull(value, "名前は必須です");
    if (value.isBlank()) {
      throw new IllegalArgumentException("名前は空文字列にできません");
    }
  }
}
