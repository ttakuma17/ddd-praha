package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.usecase.MemberService;
import com.ddd.praha.application.service.usecase.MemberTaskService;
import com.ddd.praha.application.service.usecase.TaskService;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.entity.Task;
import com.ddd.praha.domain.model.TaskId;
import com.ddd.praha.domain.model.TaskName;
import com.ddd.praha.domain.model.TaskStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<TaskResponse> findAll() {
        List<Task> tasks = taskService.findAll();
        return tasks.stream()
                .map(TaskResponse::from)
                .toList();
    }
    
    /**
     * 新しい課題を作成する
     * @param request 課題作成リクエスト
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTask(@RequestBody TaskCreateRequest request) {
        TaskName taskName = new TaskName(request.name());
        taskService.addTask(taskName);
    }
    
    /**
     * 特定の参加者の課題進捗ステータスを更新する
     * @param taskId 課題ID
     * @param request ステータス更新リクエスト
     */
    @PutMapping("/{taskId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTaskStatus(
            @PathVariable String taskId,
            @RequestBody TaskStatusUpdateRequest request) {
        Task task = taskService.get(new TaskId(taskId));
        Member member = memberService.get(new MemberId(request.memberId()));
        TaskStatus newStatus = TaskStatus.valueOf(request.status());

        memberTaskService.updateTaskStatus(member, member, task, newStatus);
    }
}