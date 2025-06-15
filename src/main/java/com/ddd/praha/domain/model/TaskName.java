package com.ddd.praha.domain.model;

import java.util.Objects;

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
