package com.ddd.praha.presentation.api;

import com.ddd.praha.domain.MemberSearchResult;
import com.ddd.praha.application.service.MemberTaskService;
import com.ddd.praha.domain.TaskId;
import com.ddd.praha.domain.TaskStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参加者課題検索コントローラー
 */
@RestController
@RequestMapping("/api/members")
public class MemberTaskSearchController {
    
    private final MemberTaskService memberTaskService;
    
    public MemberTaskSearchController(MemberTaskService memberTaskService) {
        this.memberTaskService = memberTaskService;
    }
    
    /**
     * 指定された課題群が指定されたステータスになっている参加者を10人単位でページングして取得する
     * 
     * 使用例：
     * - 「設計原則（SOLID）」と「DBモデリング１」を「レビュー待ち」している参加者を10人ずつ取得:
     *   GET /api/members/search-by-tasks?taskIds=solid&taskIds=db-modeling-1&statuses=レビュー待ち&page=0
     * - 「DBモデリング3」を「未着手」の参加者を取得:
     *   GET /api/members/search-by-tasks?taskIds=db-modeling-3&statuses=未着手&page=0
     * 
     * @param taskIds 課題IDの配列（複数指定可能）
     * @param statuses ステータスの配列（複数指定可能）
     * @param page ページ番号（0から開始、デフォルト：0）
     * @return 最大10件の参加者を含むページング結果
     */
    @GetMapping("/search-by-tasks")
    public ResponseEntity<MemberTaskSearchResponse<MemberResponse>> searchMembersByTasksAndStatuses(
            @RequestParam String[] taskIds,
            @RequestParam String[] statuses,
            @RequestParam(defaultValue = "0") int page) {
        
        // ページサイズは固定で10
        final int size = 10;
        
        // パラメータを変換
        List<TaskId> taskIdList = Arrays.stream(taskIds)
            .map(TaskId::new)
            .collect(Collectors.toList());
        
        List<TaskStatus> statusList = Arrays.stream(statuses)
            .map(TaskStatus::valueOf)
            .collect(Collectors.toList());
        
        // サービスを呼び出し
        MemberSearchResult result = memberTaskService.findMembersByTasksAndStatuses(
            taskIdList, statusList, page, size);
        
        // レスポンスを変換
        MemberTaskSearchResponse<MemberResponse> response = MemberTaskSearchResponse.fromMemberSearchResult(result);
        
        return ResponseEntity.ok(response);
    }
}