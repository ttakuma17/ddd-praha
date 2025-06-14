package com.ddd.praha.infrastructure;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ddd.praha.TestcontainersConfiguration;
import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskId;
import com.ddd.praha.domain.TaskName;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class TaskRepositoryImplTest {

  @Autowired
  private TaskRepositoryImpl taskRepository;

  @Test
  void すべての課題を取得できる() {
    Task task1 = new Task(
        new TaskId("task-1"),
        new TaskName("課題1")
    );
    Task task2 = new Task(
        new TaskId("task-2"),
        new TaskName("課題2")
    );
    taskRepository.save(task1);
    taskRepository.save(task2);

    List<Task> result = taskRepository.findAll();

    assertAll(
        () -> assertEquals(2, result.size()),
        () -> assertTrue(result.stream().anyMatch(t -> t.getId()
            .value().equals("task-1"))),
        () -> assertTrue(result.stream().anyMatch(t -> t.getId().value().equals("task-2")))
    );
  }

  @Test
  void IDで課題を取得できる() {
    Task task = new Task(
        new TaskId("test-task-id"),
        new TaskName("テスト課題")
    );
    taskRepository.save(task);

    Task result = taskRepository.get(new TaskId("test-task-id"));

    assertAll(
        () -> assertEquals("test-task-id", result.getId().value()),
        () -> assertEquals("テスト課題", result.getName().value())
    );

  }

  @Test
  void IDで課題を取得しようとして存在しない場合は例外がスローされる() {
    assertThrows(IllegalStateException.class, () ->
        taskRepository.get(new TaskId("non-existing-id"))
    );
  }


  @Test
  void 課題を新しく保存できる() {
    Task newTask = new Task(
        new TaskId("new-task-id"),
        new TaskName("新規課題")
    );

    taskRepository.save(newTask);

    Task savedTask = taskRepository.get(new TaskId("new-task-id"));
    assertAll(
        () -> assertEquals("new-task-id", savedTask.getId().value()),
        () -> assertEquals("新規課題", savedTask.getName().value())
    );
  }
}