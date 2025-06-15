package com.ddd.praha.domain;

import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.TaskProgress;
import com.ddd.praha.domain.entity.Task;
import com.ddd.praha.domain.model.Email;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberName;
import com.ddd.praha.domain.model.TaskName;
import com.ddd.praha.domain.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskProgressTest {

  private Member owner;
  private Task task1;
  private Task task2;
  private List<Task> tasks;

  @BeforeEach
  void setUp() {
    owner = new Member(
        new MemberName("山田太郎"),
        new Email("yamada@example.com"),
        EnrollmentStatus.在籍中
    );
    task1 = new Task(new TaskName("課題1"));
    task2 = new Task(new TaskName("課題2"));
    tasks = Arrays.asList(task1, task2);
  }

  @Nested
  @DisplayName("メンバータスク作成のテスト")
  class CreateTaskProgressTest {

    @Test
    @DisplayName("有効なメンバーとタスクリストでメンバータスクを作成できる")
    void createMemberTaskWithValidValues() {
      TaskProgress taskProgress = new TaskProgress(owner, tasks);

      assertEquals(owner, taskProgress.getOwner());
      assertEquals(TaskStatus.未着手, taskProgress.getTaskStatus(task1));
      assertEquals(TaskStatus.未着手, taskProgress.getTaskStatus(task2));
    }
  }

  @Nested
  @DisplayName("タスクステータス更新のテスト")
  class UpdateTaskStatusTest {

    private TaskProgress taskProgress;

    @BeforeEach
    void setUp() {
      taskProgress = new TaskProgress(owner, tasks);
    }

    @Test
    @DisplayName("タスク所有者は進捗ステータスを更新できる")
    void ownerCanUpdateTaskStatus() {
      taskProgress.updateTaskStatus(owner, task1, TaskStatus.取組中);

      assertEquals(TaskStatus.取組中, taskProgress.getTaskStatus(task1));
    }

    @Test
    @DisplayName("タスク所有者以外は進捗ステータスを更新できない")
    void nonOwnerCannotUpdateTaskStatus() {
      Member otherMember = new Member(
          new MemberName("佐藤花子"),
          new Email("sato@example.com"),
          EnrollmentStatus.在籍中
      );

      Exception exception = assertThrows(RuntimeException.class, () -> {
        taskProgress.updateTaskStatus(otherMember, task1, TaskStatus.取組中);
      });
      assertEquals("進捗ステータスを変更できるのは、課題の所有者だけです", exception.getMessage());
    }

    @Test
    @DisplayName("有効なステータス遷移の場合は更新できる")
    void updateStatusWithValidTransition() {
      // 未着手 -> 取組中 (有効な遷移)
      taskProgress.updateTaskStatus(owner, task1, TaskStatus.取組中);
      assertEquals(TaskStatus.取組中, taskProgress.getTaskStatus(task1));

      // 取組中 -> レビュー待ち (有効な遷移)
      taskProgress.updateTaskStatus(owner, task1, TaskStatus.レビュー待ち);
      assertEquals(TaskStatus.レビュー待ち, taskProgress.getTaskStatus(task1));
    }

    @Test
    @DisplayName("無効なステータス遷移の場合は例外がスローされる")
    void throwExceptionForInvalidTransition() {
      // 未着手 -> レビュー待ち (無効な遷移)
      Exception exception = assertThrows(IllegalStateException.class, () -> {
        taskProgress.updateTaskStatus(owner, task1, TaskStatus.レビュー待ち);
      });
      assertEquals("このステータス変更は許可されていません", exception.getMessage());
    }
  }

  @Nested
  @DisplayName("タスクステータス取得のテスト")
  class GetTaskStatusTest {

    private TaskProgress taskProgress;

    @BeforeEach
    void setUp() {
      taskProgress = new TaskProgress(owner, tasks);
    }

    @Test
    @DisplayName("タスクのステータスを正しく取得できる")
    void getCorrectTaskStatus() {
      assertEquals(TaskStatus.未着手, taskProgress.getTaskStatus(task1));

      taskProgress.updateTaskStatus(owner, task1, TaskStatus.取組中);
      assertEquals(TaskStatus.取組中, taskProgress.getTaskStatus(task1));
    }
  }
}