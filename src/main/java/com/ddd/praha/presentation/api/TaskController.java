package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.MemberService;
import com.ddd.praha.application.service.MemberTaskService;
import com.ddd.praha.application.service.TaskService;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberTask;
import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskId;
import com.ddd.praha.domain.TaskName;
import com.ddd.praha.domain.TaskStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 課題コントローラー
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    private final MemberService memberService;
    private final MemberTaskService memberTaskService;
    
    public TaskController(TaskService taskService, MemberService memberService, MemberTaskService memberTaskService) {
        this.taskService = taskService;
        this.memberService = memberService;
        this.memberTaskService = memberTaskService;
    }
    
    /**
     * 全ての課題を取得する
     * @return 課題のリスト
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        List<TaskResponse> response = tasks.stream()
                .map(TaskResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(response);
    }
    
    /**
     * 新しい課題を作成する
     * @param request 課題作成リクエスト
     * @return 作成された課題情報
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskCreateRequest request) {
        try {
            TaskName taskName = new TaskName(request.name());
            Task createdTask = taskService.addTask(taskName);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(TaskResponse.fromDomain(createdTask));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 特定の参加者の課題進捗ステータスを更新する
     * @param taskId 課題ID
     * @param memberId 参加者ID
     * @param request ステータス更新リクエスト
     * @return 更新された参加者課題情報
     */
    @PutMapping("/{taskId}/members/{memberId}/status")
    public ResponseEntity<MemberTaskResponse> updateTaskStatus(
            @PathVariable String taskId,
            @PathVariable String memberId,
            @RequestBody TaskStatusUpdateRequest request) {
        try {
            // 課題を取得
            Optional<Task> taskOptional = taskService.getTaskById(new TaskId(taskId));
            if (taskOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Task task = taskOptional.get();
            
            // 参加者を取得
            Optional<Member> memberOptional = memberService.getMemberById(new MemberId(memberId));
            if (memberOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Member member = memberOptional.get();
            
            // 参加者課題を取得
            Optional<MemberTask> memberTaskOptional = memberTaskService.getMemberTask(member);
            if (memberTaskOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            MemberTask memberTask = memberTaskOptional.get();
            
            // ステータスを更新
            TaskStatus newStatus = TaskStatus.valueOf(request.getStatus());
            MemberTask updatedMemberTask = memberTaskService.updateTaskStatus(member, member, task, newStatus);
            
            // レスポンスを作成
            Map<Task, TaskStatus> taskStatusMap = new HashMap<>();
            taskStatusMap.put(task, updatedMemberTask.getTaskStatus(task));
            
            return ResponseEntity.ok(MemberTaskResponse.fromDomain(updatedMemberTask, taskStatusMap));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}