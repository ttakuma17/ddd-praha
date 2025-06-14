package com.ddd.praha.domain.model;

import java.util.Objects;
import java.util.UUID;

public record TaskId(String value) {
  public TaskId {
    Objects.requireNonNull(value, "課題IDは必須です");
    if (value.isBlank()) {
      throw new IllegalArgumentException("課題IDは空文字列にできません");
    }
  }

  public static TaskId generate() {
    return new TaskId(UUID.randomUUID().toString());
  }

}
