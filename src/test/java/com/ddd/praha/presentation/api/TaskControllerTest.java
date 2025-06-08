package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.MemberService;
import com.ddd.praha.application.service.MemberTaskService;
import com.ddd.praha.application.service.TaskService;
import com.ddd.praha.domain.Email;
import com.ddd.praha.domain.EnrollmentStatus;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberName;
import com.ddd.praha.domain.MemberTask;
import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskId;
import com.ddd.praha.domain.TaskName;
import com.ddd.praha.domain.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private MemberTaskService memberTaskService;


    private Task testTask;
    private TaskId testTaskId;
    private Member testMember;
    private MemberId testMemberId;
    private MemberTask testMemberTask;

    @BeforeEach
    void setUp() {
        // テスト用のタスクを作成
        testTaskId = new TaskId("test-task-id-1");
        TaskName taskName = new TaskName("テスト課題");
        testTask = new Task(taskName) {
            @Override
            public TaskId getId() {
                return testTaskId;
            }
        };

        // テスト用のメンバーを作成
        testMemberId = new MemberId("test-member-id-1");
        MemberName name = new MemberName("テスト太郎");
        Email email = new Email("test@example.com");
        EnrollmentStatus status = EnrollmentStatus.在籍中;

        testMember = new Member(name, email, status) {
            @Override
            public MemberId getId() {
                return testMemberId;
            }
        };

        // テスト用のメンバータスクを作成
        testMemberTask = new MemberTask(testMember, Collections.singletonList(testTask));
    }

    @Test
    void updateTaskStatus_WhenSuccessful_ReturnsUpdatedTask() throws Exception {
        // リクエストの作成
        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(TaskStatus.取組中.name());

        // 更新後のメンバータスク
        MemberTask updatedMemberTask = new MemberTask(testMember, Collections.singletonList(testTask));
        updatedMemberTask.updateTaskStatus(testMember, testTask, TaskStatus.取組中);

        // モックの設定
        when(taskService.getTaskById(any(TaskId.class))).thenReturn(Optional.of(testTask));
        when(memberService.getMemberById(any(MemberId.class))).thenReturn(Optional.of(testMember));
        when(memberTaskService.getMemberTask(any(Member.class))).thenReturn(Optional.of(testMemberTask));
        when(memberTaskService.updateTaskStatus(
                any(Member.class),
                any(Member.class),
                any(Task.class),
                any(TaskStatus.class)
        )).thenReturn(updatedMemberTask);

        // タスクステータスマップの作成
        Map<Task, TaskStatus> taskStatusMap = new HashMap<>();
        taskStatusMap.put(testTask, TaskStatus.取組中);

        // APIリクエストの実行と検証
        String requestJson = """
                {
                    "status": "%s"
                }
                """.formatted(request.getStatus());

        mockMvc.perform(put("/api/tasks/{taskId}/members/{memberId}/status", testTaskId.value(), testMemberId.value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owner.id").value(testMember.getId().value()))
                .andExpect(jsonPath("$.tasks." + testTaskId.value() + ".status").value(TaskStatus.取組中.name()));
    }

    @Test
    void updateTaskStatus_WhenTaskNotFound_ReturnsNotFound() throws Exception {
        // リクエストの作成
        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(TaskStatus.取組中.name());

        // モックの設定
        when(taskService.getTaskById(any(TaskId.class))).thenReturn(Optional.empty());

        // APIリクエストの実行と検証
        String requestJson = """
                {
                    "status": "%s"
                }
                """.formatted(request.getStatus());

        mockMvc.perform(put("/api/tasks/{taskId}/members/{memberId}/status", "non-existent-task-id", testMemberId.value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTaskStatus_WhenMemberNotFound_ReturnsNotFound() throws Exception {
        // リクエストの作成
        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(TaskStatus.取組中.name());

        // モックの設定
        when(taskService.getTaskById(any(TaskId.class))).thenReturn(Optional.of(testTask));
        when(memberService.getMemberById(any(MemberId.class))).thenReturn(Optional.empty());

        // APIリクエストの実行と検証
        String requestJson = """
                {
                    "status": "%s"
                }
                """.formatted(request.getStatus());

        mockMvc.perform(put("/api/tasks/{taskId}/members/{memberId}/status", testTaskId.value(), "non-existent-member-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTaskStatus_WhenMemberTaskNotFound_ReturnsNotFound() throws Exception {
        // リクエストの作成
        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(TaskStatus.取組中.name());

        // モックの設定
        when(taskService.getTaskById(any(TaskId.class))).thenReturn(Optional.of(testTask));
        when(memberService.getMemberById(any(MemberId.class))).thenReturn(Optional.of(testMember));
        when(memberTaskService.getMemberTask(any(Member.class))).thenReturn(Optional.empty());

        // APIリクエストの実行と検証
        String requestJson = """
                {
                    "status": "%s"
                }
                """.formatted(request.getStatus());

        mockMvc.perform(put("/api/tasks/{taskId}/members/{memberId}/status", testTaskId.value(), testMemberId.value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTaskStatus_WhenStatusTransitionInvalid_ReturnsBadRequest() throws Exception {
        // リクエストの作成
        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(TaskStatus.完了.name());

        // モックの設定
        when(taskService.getTaskById(any(TaskId.class))).thenReturn(Optional.of(testTask));
        when(memberService.getMemberById(any(MemberId.class))).thenReturn(Optional.of(testMember));
        when(memberTaskService.getMemberTask(any(Member.class))).thenReturn(Optional.of(testMemberTask));
        when(memberTaskService.updateTaskStatus(
                any(Member.class),
                any(Member.class),
                any(Task.class),
                any(TaskStatus.class)
        )).thenThrow(new IllegalStateException("このステータス変更は許可されていません"));

        // APIリクエストの実行と検証
        String requestJson = """
                {
                    "status": "%s"
                }
                """.formatted(request.getStatus());

        mockMvc.perform(put("/api/tasks/{taskId}/members/{memberId}/status", testTaskId.value(), testMemberId.value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTaskStatus_WhenIllegalArgument_ReturnsBadRequest() throws Exception {
        // リクエストの作成
        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest("INVALID_STATUS");

        // モックの設定 - タスクとメンバーは存在するが、ステータスが無効
        when(taskService.getTaskById(any(TaskId.class))).thenReturn(Optional.of(testTask));
        when(memberService.getMemberById(any(MemberId.class))).thenReturn(Optional.of(testMember));
        when(memberTaskService.getMemberTask(any(Member.class))).thenReturn(Optional.of(testMemberTask));

        // APIリクエストの実行と検証
        String requestJson = """
                {
                    "status": "%s"
                }
                """.formatted(request.getStatus());

        mockMvc.perform(put("/api/tasks/{taskId}/members/{memberId}/status", testTaskId.value(), testMemberId.value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_ReturnsCreatedTask() throws Exception {
        // リクエストの作成
        TaskCreateRequest request = new TaskCreateRequest("新しい課題");

        // モックの設定
        when(taskService.addTask(any(TaskName.class))).thenReturn(testTask);

        // APIリクエストの実行と検証
        String requestJson = """
                {
                    "name": "%s"
                }
                """.formatted(request.name());

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testTask.getId().value()))
                .andExpect(jsonPath("$.name").value(testTask.getName().value()));
    }

    @Test
    void getAllTasks_ReturnsListOfTasks() throws Exception {
        // テスト用の追加タスクを作成
        TaskId task2Id = new TaskId("test-task-id-2");
        TaskName task2Name = new TaskName("テスト課題2");
        Task testTask2 = new Task(task2Name) {
            @Override
            public TaskId getId() {
                return task2Id;
            }
        };

        // モックの設定
        List<Task> tasks = Arrays.asList(testTask, testTask2);
        when(taskService.getAllTasks()).thenReturn(tasks);

        // APIリクエストの実行と検証
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testTask.getId().value()))
                .andExpect(jsonPath("$[0].name").value(testTask.getName().value()))
                .andExpect(jsonPath("$[1].id").value(testTask2.getId().value()))
                .andExpect(jsonPath("$[1].name").value(testTask2.getName().value()));
    }
}