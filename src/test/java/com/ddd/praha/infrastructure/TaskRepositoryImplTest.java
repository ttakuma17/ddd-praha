package com.ddd.praha.infrastructure;

import com.ddd.praha.TestcontainersConfiguration;
import com.ddd.praha.application.repository.TaskRepository;
import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskId;
import com.ddd.praha.domain.TaskName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TaskRepositoryImplのテスト
 * 実際のデータベースを使用した統合テスト
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
public class TaskRepositoryImplTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void findAll_ShouldReturnAllTasks() {
        // Given: テスト用タスクを作成・保存
        Task task1 = new Task(new TaskName("DDDを学ぶ"));
        Task task2 = new Task(new TaskName("テストを書く"));
        Task task3 = new Task(new TaskName("リファクタリング"));
        
        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);

        // When: 全てのタスクを取得
        List<Task> tasks = taskRepository.findAll();

        // Then: 保存したタスクが取得できること
        assertTrue(tasks.size() >= 3);
        List<String> taskNames = tasks.stream()
            .map(task -> task.getName().value())
            .toList();
        assertTrue(taskNames.contains("DDDを学ぶ"));
        assertTrue(taskNames.contains("テストを書く"));
        assertTrue(taskNames.contains("リファクタリング"));
    }

    @Test
    void findById_WithExistingId_ShouldReturnTask() {
        // Given: テスト用タスクを作成・保存
        Task testTask = new Task(new TaskName("DDDを学ぶ"));
        Task savedTask = taskRepository.save(testTask);
        TaskId existingId = savedTask.getId();

        // When: IDで検索
        Optional<Task> result = taskRepository.findById(existingId);

        // Then: タスクが取得できること
        assertTrue(result.isPresent());
        assertEquals("DDDを学ぶ", result.get().getName().value());
        assertEquals(existingId, result.get().getId());
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        // Given: 存在しないタスクID
        TaskId nonExistingId = new TaskId(UUID.randomUUID().toString());

        // When: IDで検索
        Optional<Task> result = taskRepository.findById(nonExistingId);

        // Then: 空のOptionalが返ること
        assertTrue(result.isEmpty());
    }

    @Test
    void save_WithNewTask_ShouldCreateTask() {
        // Given: 新しいタスク
        Task newTask = new Task(new TaskName("新しいタスク"));

        // When: 保存
        Task savedTask = taskRepository.save(newTask);

        // Then: 保存されたタスクが返ること
        assertNotNull(savedTask);
        assertNotNull(savedTask.getId());
        assertEquals("新しいタスク", savedTask.getName().value());

        // And: データベースから取得できること
        Optional<Task> retrieved = taskRepository.findById(savedTask.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("新しいタスク", retrieved.get().getName().value());
    }

    @Test
    void save_WithExistingTask_ShouldUpdateTask() {
        // Given: テスト用タスクを作成・保存
        Task originalTask = new Task(new TaskName("更新前タスク"));
        Task savedOriginalTask = taskRepository.save(originalTask);
        TaskId existingId = savedOriginalTask.getId();
        
        // And: タスク名を変更
        Task updatedTask = new Task(existingId, new TaskName("更新されたタスク名"));

        // When: 保存（更新）
        Task savedTask = taskRepository.save(updatedTask);

        // Then: 更新されたタスクが返ること
        assertEquals("更新されたタスク名", savedTask.getName().value());
        assertEquals(existingId, savedTask.getId());

        // And: データベースでも更新されていること
        Optional<Task> retrieved = taskRepository.findById(existingId);
        assertTrue(retrieved.isPresent());
        assertEquals("更新されたタスク名", retrieved.get().getName().value());
    }

    @Test
    void save_WithMultipleTasks_ShouldMaintainAllTasks() {
        // Given: 複数の新しいタスク
        Task task1 = new Task(new TaskName("タスク1"));
        Task task2 = new Task(new TaskName("タスク2"));
        Task task3 = new Task(new TaskName("タスク3"));

        // When: 全て保存
        Task savedTask1 = taskRepository.save(task1);
        Task savedTask2 = taskRepository.save(task2);
        Task savedTask3 = taskRepository.save(task3);

        // Then: 保存したタスクが全て取得できること
        List<Task> allTasks = taskRepository.findAll();
        assertTrue(allTasks.size() >= 3);
        
        // And: 保存したタスクが全て含まれていること
        List<String> taskNames = allTasks.stream()
            .map(task -> task.getName().value())
            .toList();
        assertTrue(taskNames.contains("タスク1"));
        assertTrue(taskNames.contains("タスク2"));
        assertTrue(taskNames.contains("タスク3"));
        
        // And: 各タスクがIDで取得できること
        assertTrue(taskRepository.findById(savedTask1.getId()).isPresent());
        assertTrue(taskRepository.findById(savedTask2.getId()).isPresent());
        assertTrue(taskRepository.findById(savedTask3.getId()).isPresent());
    }
}