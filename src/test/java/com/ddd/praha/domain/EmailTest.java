package com.ddd.praha.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.ddd.praha.domain.model.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {

  @Nested
  @DisplayName("Emailの作成テスト")
  class CreateEmailTest {

    @Test
    @DisplayName("有効なメールアドレスでインスタンスを作成できる")
    void createValidEmail() {
      String validAddress = "test@example.com";
      Email email = new Email(validAddress);
      assertEquals(validAddress, email.value());
    }

    @ParameterizedTest
    @DisplayName("無効なメールアドレスでインスタンスを作成すると例外がスローされる")
    @ValueSource(strings = {
        "invalid-email",
        "@example.com",
        "test@",
        "test@example.",
        "test@.com",
        ""
    })
    void throwExceptionForInvalidEmail(String invalidAddress) {
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        new Email(invalidAddress);
      });
      assertEquals("不正なメールアドレス形式です: " + invalidAddress, exception.getMessage());
    }
  }
}