package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.usecase.MemberService;
import com.ddd.praha.domain.model.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 参加者課題検索コントローラー
 */
@RestController
@RequestMapping("/api/search")
public class MemberSearchController {

  private final MemberService memberService;
  
  public MemberSearchController(MemberService memberService) {
    this.memberService = memberService;
  }


  /**
   * 仕様
   *
 * 「特定の課題（複数可能）」が「特定の進捗ステータス」になっている参加者の一覧を、10人単位でページングして取得する
   *  - 例１：「設計原則（SOLID）」と「DBモデリング１」を「レビュー完了」している参加者一覧を取得する
   *  - 例２：「DBモデリング3」を「未着手」の参加者一覧を取得する
   *  - 条件に合致する参加者を全て取得するのではなく、10名ずつ取得する点（ページング）にご注意ください！
   */
  @PostMapping("/members")
  public MembersResponse searchMembersByTasksAndStatuses(@Valid @RequestBody MemberSearchRequest request) {
    // TaskIdとTaskStatusの変換
    List<TaskId> taskIds = request.taskIds().stream()
        .map(TaskId::new)
        .collect(Collectors.toList());
    
    List<TaskStatus> statuses = request.statuses().stream()
        .map(TaskStatus::valueOf)
        .collect(Collectors.toList());
    
    // サービス層の呼び出し（固定で10件ずつ取得）
    MemberSearchResult result = memberService.searchMembersByTasksAndStatuses(
        taskIds,
        statuses,
        request.page(),
        10
    );
    
    return MembersResponse.from(result);
  }
}