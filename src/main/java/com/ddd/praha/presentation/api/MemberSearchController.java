package com.ddd.praha.presentation.api;

import com.ddd.praha.domain.model.MemberSearchResult;
import com.ddd.praha.application.service.usecase.MemberTaskService;
import com.ddd.praha.domain.model.TaskId;
import com.ddd.praha.domain.model.TaskStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参加者課題検索コントローラー
 */
@RestController
@RequestMapping("/api/search")
public class MemberSearchController {

  private final MemberTaskService memberTaskService;

  public MemberSearchController(MemberTaskService memberTaskService) {
    this.memberTaskService = memberTaskService;
  }

  /**
   * 指定された課題群が指定されたステータスになっている参加者を10人単位でページングして取得する
   * <p>
   * 使用例： - 「設計原則（SOLID）」と「DBモデリング１」を「レビュー待ち」している参加者を10人ずつ取得: POST /api/members/search-by-tasks
   * {"taskIds": ["solid", "db-modeling-1"], "statuses": ["レビュー待ち"], "page": 0} -
   * 「DBモデリング3」を「未着手」の参加者を取得: POST /api/members/search-by-tasks {"taskIds": ["db-modeling-3"],
   * "statuses": ["未着手"], "page": 0}
   *
   * @param request 検索条件（課題ID配列、ステータス配列、ページ番号）
   * @return 最大10件の参加者を含むページング結果
   */
  @PostMapping("/search-by-tasks")
  public MemberSearchResponse<MemberResponse> searchMembersByTasksAndStatuses(
      @RequestBody MemberSearchRequest request) {

    // ページサイズは固定で10
    final int size = 10;

    // パラメータを変換
    List<TaskId> taskIdList = Arrays.stream(request.getTaskIds())
        .map(TaskId::new)
        .collect(Collectors.toList());

    List<TaskStatus> statusList = Arrays.stream(request.getStatuses())
        .map(TaskStatus::valueOf)
        .collect(Collectors.toList());

    // サービスを呼び出し
    MemberSearchResult result = memberTaskService.findMembersByTasksAndStatuses(
        taskIdList, statusList, request.getPage(), size);
    return MemberSearchResponse.from(result);
  }
}