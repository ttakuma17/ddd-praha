package com.ddd.praha.infrastructure;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ddd.praha.annotation.MyBatisRepositoryTest;
import com.ddd.praha.domain.entity.Task;
import com.ddd.praha.domain.model.TaskId;
import com.ddd.praha.domain.model.TaskName;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@MyBatisRepositoryTest
class TaskRepositoryImplTest {

  @Autowired
  private TaskRepositoryImpl taskRepository;

  @Test
  void すべての課題を取得できる() {
    String taskId1 = "task-" + UUID.randomUUID().toString();
    String taskId2 = "task-" + UUID.randomUUID().toString();
    
    Task task1 = new Task(
        new TaskId(taskId1),
        new TaskName("課題1")
    );
    Task task2 = new Task(
        new TaskId(taskId2),
        new TaskName("課題2")
    );
    taskRepository.save(task1);
    taskRepository.save(task2);

    List<Task> result = taskRepository.findAll();

    assertAll(
        () -> assertTrue(result.size() >= 2),
        () -> assertTrue(result.stream().anyMatch(t -> t.getId()
            .value().equals(taskId1))),
        () -> assertTrue(result.stream().anyMatch(t -> t.getId().value().equals(taskId2)))
    );
  }

  @Test
  void IDで課題を取得できる() {
    String taskId = "test-task-" + UUID.randomUUID();
    
    Task task = new Task(
        new TaskId(taskId),
        new TaskName("テスト課題")
    );
    taskRepository.save(task);

    Task result = taskRepository.get(new TaskId(taskId));

    assertAll(
        () -> assertEquals(taskId, result.getId().value()),
        () -> assertEquals("テスト課題", result.getName().value())
    );
  }

  @Test
  void IDで課題を取得しようとして存在しない場合は例外がスローされる() {
    String nonExistingId = "non-existing-" + UUID.randomUUID();
    
    assertThrows(IllegalStateException.class, () ->
        taskRepository.get(new TaskId(nonExistingId))
    );
  }

  @Test
  void 課題を新しく保存できる() {
    String taskId = "new-task-" + UUID.randomUUID();
    
    Task newTask = new Task(
        new TaskId(taskId),
        new TaskName("新規課題")
    );

    taskRepository.save(newTask);

    Task savedTask = taskRepository.get(new TaskId(taskId));
    assertAll(
        () -> assertEquals(taskId, savedTask.getId().value()),
        () -> assertEquals("新規課題", savedTask.getName().value())
    );
  }
}