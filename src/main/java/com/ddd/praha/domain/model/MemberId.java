package com.ddd.praha.domain.model;

import java.util.Objects;
import java.util.UUID;

public record MemberId(String value) {
  public MemberId {
    Objects.requireNonNull(value, "参加者IDは必須です");
    if (value.isBlank()) {
      throw new IllegalArgumentException("参加者IDは空文字列にできません");
    }
  }

  public static MemberId generate() {
    return new MemberId(UUID.randomUUID().toString());
  }
}
