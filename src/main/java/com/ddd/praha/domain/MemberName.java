package com.ddd.praha.domain;

import java.util.Objects;

public record MemberName(String value) {
  public MemberName {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("名前は必須です");
    }
    if (value.length() > 30) {
      throw new IllegalArgumentException("名前は30文字以内にしてください");
    }
  }
}
