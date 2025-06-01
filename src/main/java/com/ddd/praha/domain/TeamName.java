package com.ddd.praha.domain;

public record TeamName(String value) {

  public TeamName {
    if (!value.matches("[a-zA-Z]+")) {
      throw new IllegalArgumentException("チームの名前は英文字でなければいけません");
    }
  }
}
