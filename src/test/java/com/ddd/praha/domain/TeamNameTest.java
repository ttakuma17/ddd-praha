package com.ddd.praha.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TeamNameTest {

  @Test
  @DisplayName("有効なチーム名でインスタンスを作成できる")
  void createValidTeamName() {
    String validName = "チームA";
    TeamName teamName = new TeamName(validName);
    assertEquals(validName, teamName.value());
  }

  @Test
  @DisplayName("nullのチーム名で作成すると例外がスローされる")
  void throwExceptionForNullTeamName() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      new TeamName(null);
    });
    assertEquals("チーム名は必須です", exception.getMessage());
  }

  @ParameterizedTest
  @DisplayName("空のチーム名で作成すると例外がスローされる")
  @ValueSource(strings = {"", " ", "　"})
  void throwExceptionForEmptyTeamName(String emptyName) {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      new TeamName(emptyName);
    });
    assertEquals("チーム名は必須です", exception.getMessage());
  }

  @Test
  @DisplayName("長すぎるチーム名で作成すると例外がスローされる")
  void throwExceptionForTooLongTeamName() {
    String longName = "a".repeat(21); // 21文字の名前
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      new TeamName(longName);
    });
    assertEquals("チーム名は20文字以内にしてください", exception.getMessage());
  }
}