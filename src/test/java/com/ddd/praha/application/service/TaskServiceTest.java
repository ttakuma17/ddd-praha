package com.ddd.praha.application.service;

import com.ddd.praha.application.repository.TaskRepository;
import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskId;
import com.ddd.praha.domain.TaskName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    
    private TaskService taskService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskService = new TaskService(taskRepository);
    }
    
    @Test
    @DisplayName("新しい課題を追加できる")
    void addTask_ReturnsCreatedTask() {
        // 準備
        TaskName taskName = new TaskName("新しい課題");
        Task expectedTask = new Task(taskName) {
            @Override
            public TaskId getId() {
                return new TaskId("test-task-id");
            }
        };
        
        // モックの設定
        when(taskRepository.save(any(Task.class))).thenReturn(expectedTask);
        
        // 実行
        Task result = taskService.addTask(taskName);
        
        // 検証
        assertThat(result).isNotNull();
        assertThat(result.getId().value()).isEqualTo("test-task-id");
        assertThat(result.getName().value()).isEqualTo("新しい課題");
    }
}