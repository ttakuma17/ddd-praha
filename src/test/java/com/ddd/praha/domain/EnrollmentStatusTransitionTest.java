package com.ddd.praha.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.EnrollmentStatusTransition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EnrollmentStatusTransitionTest {

  @Test
  @DisplayName("EnrollmentStatusTransitionのインスタンスを作成できる")
  void 在籍ステータス遷移のインスタンスを作成できる() {
    EnrollmentStatusTransition transition = new EnrollmentStatusTransition();
    assertNotNull(transition);
  }

  @ParameterizedTest
  @DisplayName("有効な状態遷移を確認できる")
  @CsvSource({
      "在籍中, 休会中",
      "在籍中, 退会済",
      "休会中, 在籍中",
      "休会中, 退会済",
      "退会済, 在籍中"
  })
  void 有効な状態遷移を確認できる(EnrollmentStatus from, EnrollmentStatus to) {
    EnrollmentStatusTransition transition = new EnrollmentStatusTransition();
    assertTrue(transition.canTransit(from, to));
  }

  @ParameterizedTest
  @DisplayName("無効な状態遷移は許可されない")
  @CsvSource({
      "退会済, 休会中"
  })
  void 無効な状態遷移は許可されない(EnrollmentStatus from, EnrollmentStatus to) {
    EnrollmentStatusTransition transition = new EnrollmentStatusTransition();
    assertFalse(transition.canTransit(from, to));
  }

  @Test
  @DisplayName("同じ状態への遷移は許可されない")
  void 同じ状態への遷移は許可されない() {
    EnrollmentStatusTransition transition = new EnrollmentStatusTransition();
    assertFalse(transition.canTransit(EnrollmentStatus.在籍中, EnrollmentStatus.在籍中));
    assertFalse(transition.canTransit(EnrollmentStatus.休会中, EnrollmentStatus.休会中));
    assertFalse(transition.canTransit(EnrollmentStatus.退会済, EnrollmentStatus.退会済));
  }
}