package com.ddd.praha.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.ddd.praha.domain.model.MemberName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MemberNameTest {

  @Test
  @DisplayName("有効なメンバー名でインスタンスを作成できる")
  void 有効なメンバー名でインスタンスを作成できる() {
    String validName = "山田太郎";
    MemberName memberName = new MemberName(validName);
    assertEquals(validName, memberName.value());
  }

  @Test
  @DisplayName("nullのメンバー名で作成すると例外がスローされる")
  void nullのメンバー名で作成すると例外がスローされる() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      new MemberName(null);
    });
    assertEquals("名前は必須です", exception.getMessage());
  }

  @ParameterizedTest
  @DisplayName("空のメンバー名で作成すると例外がスローされる")
  @ValueSource(strings = {"", " ", "　"})
  void 空のメンバー名で作成すると例外がスローされる(String emptyName) {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      new MemberName(emptyName);
    });
    assertEquals("名前は必須です", exception.getMessage());
  }

  @Test
  @DisplayName("長すぎるメンバー名で作成すると例外がスローされる")
  void 長すぎるメンバー名で作成すると例外がスローされる() {
    String longName = "a".repeat(31); // 31文字の名前
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      new MemberName(longName);
    });
    assertEquals("名前は30文字以内にしてください", exception.getMessage());
  }
}