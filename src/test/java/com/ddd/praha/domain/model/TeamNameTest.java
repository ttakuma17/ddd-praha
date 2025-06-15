package com.ddd.praha.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TeamNameTest {

    @Test
    void 正常系_有効な英文字のチーム名() {
        // Given & When & Then
        assertDoesNotThrow(() -> new TeamName("TeamAlpha"));
        assertDoesNotThrow(() -> new TeamName("A"));
        assertDoesNotThrow(() -> new TeamName("ABCDEFGHIJKLMNOPQRST")); // 20文字
    }

    @Test
    void 異常系_null値は例外() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new TeamName(null)
        );
        assertEquals("チーム名は必須です", exception.getMessage());
    }

    @Test
    void 異常系_空文字列は例外() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new TeamName("")
        );
        assertEquals("チーム名は必須です", exception.getMessage());
    }

    @Test
    void 異常系_空白文字のみは例外() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new TeamName("   ")
        );
        assertEquals("チーム名は必須です", exception.getMessage());
    }

    @Test
    void 異常系_21文字以上は例外() {
        // Given
        String longName = "ABCDEFGHIJKLMNOPQRSTU"; // 21文字

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new TeamName(longName)
        );
        assertEquals("チーム名は20文字以内にしてください", exception.getMessage());
    }

    @Test
    void 異常系_日本語文字が含まれると例外() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new TeamName("チームA")
        );
        assertEquals("チーム名は英文字のみ使用できます", exception.getMessage());
    }

    @Test
    void 異常系_数字が含まれると例外() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new TeamName("Team1")
        );
        assertEquals("チーム名は英文字のみ使用できます", exception.getMessage());
    }

    @Test
    void 異常系_記号が含まれると例外() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new TeamName("Team-A")
        );
        assertEquals("チーム名は英文字のみ使用できます", exception.getMessage());
    }

    @Test
    void 異常系_小文字と大文字混在でも英文字なら正常() {
        // Given & When & Then
        assertDoesNotThrow(() -> new TeamName("TeamAlpha"));
        assertDoesNotThrow(() -> new TeamName("teamalpha"));
        assertDoesNotThrow(() -> new TeamName("TEAMALPHA"));
    }
}