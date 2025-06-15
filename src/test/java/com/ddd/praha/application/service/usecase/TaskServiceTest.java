package com.ddd.praha.application.service.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.ddd.praha.application.repository.MemberTaskRepository;
import com.ddd.praha.application.repository.TaskRepository;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.MemberTask;
import com.ddd.praha.domain.entity.Task;
import com.ddd.praha.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MemberTaskRepository memberTaskRepository;

    private TaskService taskService;

    private Member testMember;
    private Member otherMember;
    private Task testTask;
    private MemberTask memberTask;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, memberTaskRepository);

        testMember = new Member(
            new MemberName("テストユーザー"),
            new Email("test@example.com"),
            EnrollmentStatus.在籍中
        );

        otherMember = new Member(
            new MemberName("他のユーザー"),
            new Email("other@example.com"),
            EnrollmentStatus.在籍中
        );

        testTask = new Task(new TaskName("テスト課題"));

        Map<Task, TaskStatus> taskMap = new HashMap<>();
        taskMap.put(testTask, TaskStatus.未着手);
        memberTask = new MemberTask(testMember, taskMap);
    }

    @Test
    void updateTaskStatus_正常系_課題ステータスが更新される() {
        // Given
        when(memberTaskRepository.findByMemberAndTask(testMember, testTask))
            .thenReturn(memberTask);

        // When
        taskService.updateTaskStatus(testMember, testMember, testTask, TaskStatus.取組中);

        // Then
        verify(memberTaskRepository).save(memberTask, testTask);
        assertEquals(TaskStatus.取組中, memberTask.getTaskStatus(testTask));
    }

    @Test
    void updateTaskStatus_異常系_別の参加者が更新しようとすると例外() {
        // Given
        when(memberTaskRepository.findByMemberAndTask(testMember, testTask))
            .thenReturn(memberTask);

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> taskService.updateTaskStatus(otherMember, testMember, testTask, TaskStatus.取組中)
        );

        assertEquals("進捗ステータスを変更できるのは、課題の所有者だけです", exception.getMessage());
        verify(memberTaskRepository, never()).save(any(), any());
    }

    @Test
    void updateTaskStatus_異常系_不正なステータス遷移で例外() {
        // Given
        when(memberTaskRepository.findByMemberAndTask(testMember, testTask))
            .thenReturn(memberTask);

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> taskService.updateTaskStatus(testMember, testMember, testTask, TaskStatus.完了)
        );

        assertEquals("このステータス変更は許可されていません", exception.getMessage());
        verify(memberTaskRepository, never()).save(any(), any());
    }

    @Test
    void updateTaskStatus_異常系_課題が存在しない場合は例外() {
        // Given
        when(memberTaskRepository.findByMemberAndTask(testMember, testTask))
            .thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> taskService.updateTaskStatus(testMember, testMember, testTask, TaskStatus.取組中)
        );

        assertEquals("指定された課題が見つかりません", exception.getMessage());
        verify(memberTaskRepository, never()).save(any(), any());
    }
}