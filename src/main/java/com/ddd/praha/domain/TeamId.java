package com.ddd.praha.domain;

import java.util.Objects;
import java.util.UUID;

public record TeamId(String value) {
  public TeamId {
    Objects.requireNonNull(value, "チームIDは必須です");
    if (value.isBlank()) {
      throw new IllegalArgumentException("チームIDは空文字列にできません");
    }
  }

  public static TeamId generate() {
    return new TeamId(UUID.randomUUID().toString());
  }
}
