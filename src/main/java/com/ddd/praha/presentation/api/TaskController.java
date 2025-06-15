package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.usecase.MemberService;
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

/**
 * 課題管理のREST APIコントローラ。
 * 
 * <p>プラハチャレンジの課題に関するHTTPエンドポイントを提供する。
 * 課題の一覧取得、新規作成、進捗ステータス更新の機能を持つ。</p>
 * 
 * <p>提供するエンドポイント：</p>
 * <ul>
 *   <li>GET /api/tasks - 全課題の一覧取得</li>
 *   <li>POST /api/tasks - 新しい課題の作成</li>
 *   <li>PUT /api/tasks/{taskId}/status - 課題進捗ステータスの更新</li>
 * </ul>
 * 
 * <p>このコントローラはHTTPリクエストをドメインモデルに変換し、
 * アプリケーションサービス層に処理を委譲する責務を持つ。</p>
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    private final MemberService memberService;

    /**
     * TaskControllerのコンストラクタ。
     * 
     * @param taskService 課題サービス
     * @param memberService メンバーサービス
     */
    public TaskController(TaskService taskService, MemberService memberService) {
        this.taskService = taskService;
        this.memberService = memberService;
    }
    
    /**
     * 全ての課題を取得し、HTTPレスポンス形式で返す。
     * 
     * <p>システムに登録されている全ての学習課題を一覧取得する。
     * ドメインモデルからAPIレスポンス形式への変換も行う。</p>
     * 
     * @return 課題情報のレスポンスリスト
     */
    @GetMapping
    public List<TaskResponse> findAll() {
        List<Task> tasks = taskService.findAll();
        return tasks.stream()
                .map(TaskResponse::from)
                .toList();
    }
    
    /**
     * 新しい課題を作成する。
     * 
     * <p>HTTPリクエストから課題名を抽出し、
     * ドメインモデルへ変換して課題を作成する。</p>
     * 
     * @param request 課題作成リクエスト（課題名を含む）
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTask(@RequestBody TaskCreateRequest request) {
        TaskName taskName = new TaskName(request.name());
        taskService.addTask(taskName);
    }
    
    /**
     * 特定の参加者の課題進捗ステータスを更新する。
     * 
     * <p>HTTPリクエストから課題ID、メンバーID、新しいステータスを抽出し、
     * ドメインモデルへ変換して進捗更新を実行する。
     * 権限チェックやステータス遷移検証はサービス層で実行される。</p>
     * 
     * @param taskId 課題ID（URLパスパラメータ）
     * @param request ステータス更新リクエスト（メンバーIDと新ステータスを含む）
     */
    @PutMapping("/{taskId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTaskStatus(
            @PathVariable String taskId,
            @RequestBody TaskStatusUpdateRequest request) {
        Task task = taskService.get(new TaskId(taskId));
        Member member = memberService.get(new MemberId(request.memberId()));
        TaskStatus newStatus = TaskStatus.valueOf(request.status());

        taskService.updateTaskStatus(member, member, task, newStatus);
    }
}