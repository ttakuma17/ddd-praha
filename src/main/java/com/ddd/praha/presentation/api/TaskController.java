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
import com.ddd.praha.presentation.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
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
    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return tasks.stream()
                .map(TaskResponse::from)
                .toList();
    }
    
    /**
     * 新しい課題を作成する
     * @param request 課題作成リクエスト
     * @return 作成された課題情報
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@RequestBody TaskCreateRequest request) {
        TaskName taskName = new TaskName(request.name());
        Task createdTask = taskService.addTask(taskName);
        return TaskResponse.from(createdTask);
    }
    
    /**
     * 特定の参加者の課題進捗ステータスを更新する
     * @param taskId 課題ID
     * @param memberId 参加者ID
     * @param request ステータス更新リクエスト
     * @return 更新された参加者課題情報
     */
    @PutMapping("/{taskId}/members/{memberId}/status")
    public MemberTaskResponse updateTaskStatus(
            @PathVariable String taskId,
            @PathVariable String memberId,
            @RequestBody TaskStatusUpdateRequest request) {
        Task task = taskService.getTaskById(new TaskId(taskId));
        if (task == null) {
            throw new ResourceNotFoundException("Task not found: " + taskId);
        }

        Member member = memberService.get(new MemberId(memberId));

        
        Optional<MemberTask> memberTaskOptional = memberTaskService.getMemberTask(member);
        if (memberTaskOptional.isEmpty()) {
            throw new ResourceNotFoundException("MemberTask not found for member: " + memberId);
        }
        
        TaskStatus newStatus = TaskStatus.valueOf(request.getStatus());
        MemberTask updatedMemberTask = memberTaskService.updateTaskStatus(member, member, task, newStatus);
        
        Map<Task, TaskStatus> taskStatusMap = new HashMap<>();
        taskStatusMap.put(task, updatedMemberTask.getTaskStatus(task));
        
        return MemberTaskResponse.fromDomain(updatedMemberTask, taskStatusMap);
    }
}