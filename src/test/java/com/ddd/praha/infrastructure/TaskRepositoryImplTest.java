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

import static org.assertj.core.api.Assertions.assertThat;

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
        // Given: Flywayマイグレーションでテストデータが投入されている

        // When: 全てのタスクを取得
        List<Task> tasks = taskRepository.findAll();

        // Then: 3件のタスクが取得できること（V2__Insert_test_data.sqlのデータ）
        assertThat(tasks).hasSize(3);
        assertThat(tasks)
            .extracting(task -> task.getName().value())
            .containsExactlyInAnyOrder("DDDを学ぶ", "テストを書く", "リファクタリング");
    }

    @Test
    void findById_WithExistingId_ShouldReturnTask() {
        // Given: 既存のタスクID
        TaskId existingId = new TaskId("770e8400-e29b-41d4-a716-446655440001");

        // When: IDで検索
        Optional<Task> result = taskRepository.findById(existingId);

        // Then: タスクが取得できること
        assertThat(result).isPresent();
        assertThat(result.get().getName().value()).isEqualTo("DDDを学ぶ");
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        // Given: 存在しないタスクID
        TaskId nonExistingId = new TaskId(UUID.randomUUID().toString());

        // When: IDで検索
        Optional<Task> result = taskRepository.findById(nonExistingId);

        // Then: 空のOptionalが返ること
        assertThat(result).isEmpty();
    }

    @Test
    void save_WithNewTask_ShouldCreateTask() {
        // Given: 新しいタスク
        Task newTask = new Task(new TaskName("新しいタスク"));

        // When: 保存
        Task savedTask = taskRepository.save(newTask);

        // Then: 保存されたタスクが返ること
        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getName().value()).isEqualTo("新しいタスク");

        // And: データベースから取得できること
        Optional<Task> retrieved = taskRepository.findById(savedTask.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName().value()).isEqualTo("新しいタスク");
    }

    @Test
    void save_WithExistingTask_ShouldUpdateTask() {
        // Given: 既存のタスクを取得
        TaskId existingId = new TaskId("770e8400-e29b-41d4-a716-446655440001");
        Task existingTask = taskRepository.findById(existingId).orElseThrow();
        
        // And: タスク名を変更
        Task updatedTask = new Task(existingId, new TaskName("更新されたタスク名"));

        // When: 保存（更新）
        Task savedTask = taskRepository.save(updatedTask);

        // Then: 更新されたタスクが返ること
        assertThat(savedTask.getName().value()).isEqualTo("更新されたタスク名");

        // And: データベースでも更新されていること
        Optional<Task> retrieved = taskRepository.findById(existingId);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName().value()).isEqualTo("更新されたタスク名");
    }

    @Test
    void save_WithMultipleTasks_ShouldMaintainAllTasks() {
        // Given: 複数の新しいタスク
        Task task1 = new Task(new TaskName("タスク1"));
        Task task2 = new Task(new TaskName("タスク2"));
        Task task3 = new Task(new TaskName("タスク3"));

        // When: 全て保存
        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);

        // Then: 全てのタスクが取得できること（既存の3件 + 新規3件）
        List<Task> allTasks = taskRepository.findAll();
        assertThat(allTasks).hasSize(6);
    }
}