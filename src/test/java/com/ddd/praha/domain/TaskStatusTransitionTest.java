package com.ddd.praha.domain;

import com.ddd.praha.domain.model.TaskStatus;
import com.ddd.praha.domain.model.TaskStatusTransition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskStatusTransitionTest {

  @Test
  @DisplayName("未着手から取組中への遷移は許可される")
  void 未着手から取組中への遷移は許可される() {
    TaskStatusTransition transition = new TaskStatusTransition();
    boolean canTransit = transition.canTransit(TaskStatus.未着手, TaskStatus.取組中);
    assertTrue(canTransit);
  }

  @Test
  @DisplayName("未着手からレビュー待ちへの遷移は許可されない")
  void 未着手からレビュー待ちへの遷移は許可されない() {
    TaskStatusTransition transition = new TaskStatusTransition();
    boolean canTransit = transition.canTransit(TaskStatus.未着手, TaskStatus.レビュー待ち);
    assertFalse(canTransit);
  }

  @Test
  @DisplayName("未着手から完了への遷移は許可されない")
  void 未着手から完了への遷移は許可されない() {
    TaskStatusTransition transition = new TaskStatusTransition();
    boolean canTransit = transition.canTransit(TaskStatus.未着手, TaskStatus.完了);
    assertFalse(canTransit);
  }

  @Test
  @DisplayName("取組中からレビュー待ちへの遷移は許可される")
  void 取組中からレビュー待ちへの遷移は許可される() {
    TaskStatusTransition transition = new TaskStatusTransition();
    boolean canTransit = transition.canTransit(TaskStatus.取組中, TaskStatus.レビュー待ち);
    assertTrue(canTransit);
  }

  @Test
  @DisplayName("取組中から完了への遷移は許可されない")
  void 取組中から完了への遷移は許可されない() {
    TaskStatusTransition transition = new TaskStatusTransition();
    boolean canTransit = transition.canTransit(TaskStatus.取組中, TaskStatus.完了);
    assertFalse(canTransit);
  }

  @Test
  @DisplayName("レビュー待ちから取組中への遷移は許可される")
  void レビュー待ちから取組中への遷移は許可される() {
    TaskStatusTransition transition = new TaskStatusTransition();
    boolean canTransit = transition.canTransit(TaskStatus.レビュー待ち, TaskStatus.取組中);
    assertTrue(canTransit);
  }

  @Test
  @DisplayName("レビュー待ちから完了への遷移は許可される")
  void レビュー待ちから完了への遷移は許可される() {
    TaskStatusTransition transition = new TaskStatusTransition();
    boolean canTransit = transition.canTransit(TaskStatus.レビュー待ち, TaskStatus.完了);
    assertTrue(canTransit);
  }
}