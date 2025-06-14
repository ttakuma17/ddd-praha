package com.ddd.praha.presentation.api;

import org.springframework.web.bind.annotation.*;

/**
 * 参加者課題検索コントローラー
 */
@RestController
@RequestMapping("/api/search")
public class MemberSearchController {


  /**
   * 仕様
   *
   * 「特定の課題（複数可能）」が「特定の進捗ステータス」になっている参加者の一覧を、10人単位でページングして取得する
   *  - 例１：「設計原則（SOLID）」と「DBモデリング１」を「レビュー完了」している参加者一覧を取得する
   *  - 例２：「DBモデリング3」を「未着手」の参加者一覧を取得する
   *  - 条件に合致する参加者を全て取得するのではなく、10名ずつ取得する点（ページング）にご注意ください！
   */
  @PostMapping("/members")
  public MembersResponse searchMembersByTasksAndStatuses()(@RequestBody MemberSearchRequest request) {
    // todo
    return new MembersResponse();
  }
}