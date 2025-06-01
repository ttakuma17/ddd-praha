package com.ddd.praha.presentation.api;

import com.ddd.praha.presentation.request.TaskStatusUpdateRequest;
import com.ddd.praha.presentation.response.MemberTaskResponse;
import com.ddd.praha.application.service.MemberService;
import com.ddd.praha.application.service.MemberTaskService;
import com.ddd.praha.application.service.TaskService;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberTask;
import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskId;
import com.ddd.praha.domain.TaskStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
            if (!taskOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            Task task = taskOptional.get();
            
            // 参加者を取得
            Optional<Member> memberOptional = memberService.getMemberById(new MemberId(memberId));
            if (!memberOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            Member member = memberOptional.get();
            
            // 参加者課題を取得
            Optional<MemberTask> memberTaskOptional = memberTaskService.getMemberTask(member);
            if (!memberTaskOptional.isPresent()) {
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}