package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.usecase.MemberService;
import com.ddd.praha.application.service.usecase.TeamQueryService;
import com.ddd.praha.application.service.usecase.TeamOrchestrationService;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.entity.Team;
import com.ddd.praha.domain.model.TeamId;
import org.springframework.web.bind.annotation.*;
import com.ddd.praha.presentation.exception.ResourceNotFoundException;
import com.ddd.praha.presentation.exception.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * チーム管理のREST APIコントローラー。
 * 
 * <p>チームに関する照会・更新操作を提供するRESTfulなAPIエンドポイントを定義する。
 * チーム一覧取得、詳細取得、メンバー編成更新機能を提供している。</p>
 * 
 * <p>提供するエンドポイント：</p>
 * <ul>
 *   <li>GET /api/teams - 全チームの一覧取得</li>
 *   <li>GET /api/teams/{id} - 特定チームの詳細取得</li>
 *   <li>PUT /api/teams/{id}/members - チームメンバーの編成更新</li>
 * </ul>
 * 
 * <p>チームメンバーの更新では、自動的なチーム分割・合流ロジックが適用される。</p>
 * 
 */
@RestController
@RequestMapping("/api/teams")
public class TeamController {

  private final TeamQueryService teamQueryService;
  private final TeamOrchestrationService teamOrchestrationService;
  private final MemberService memberService;

  public TeamController(TeamQueryService teamQueryService,
      TeamOrchestrationService teamOrchestrationService,
      MemberService memberService) {
    this.teamQueryService = teamQueryService;
    this.teamOrchestrationService = teamOrchestrationService;
    this.memberService = memberService;
  }

  /**
   * 全てのチームを取得する
   *
   * @return チームのリスト
   */
  @GetMapping
  public List<TeamResponse> listAll() {
    List<Team> teams = teamQueryService.getAll();
    return teams.stream()
        .map(TeamResponse::from)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public TeamResponse get(@PathVariable String id) {
    Team team = teamQueryService.get(new TeamId(id));
    if (team == null) {
      throw new ResourceNotFoundException("Team not found with id: " + id);
    }
    return TeamResponse.from(team);
  }

  /**
   * チームのメンバーを更新する
   *
   * @param id      チームID
   * @param request メンバー更新リクエスト
   * @return 更新されたチーム情報
   */
  @PutMapping("/{id}/members")
  public TeamResponse updateTeamMembers(
      @PathVariable String id,
      @RequestBody TeamMemberUpdateRequest request) {

    // チームを取得
    Team team = teamQueryService.get(new TeamId(id));
    if (team == null) {
      throw new ResourceNotFoundException("Team not found with id: " + id);
    }
    TeamId teamId = team.getId();

    try {
      // 現在のメンバーと新しいメンバーを取得
      List<Member> currentMembers = new ArrayList<>(team.getMembers());
      List<Member> newMembers = request.getMemberIds().stream()
          .map(memberId -> {
            try {
              return memberService.get(new MemberId(memberId));
            } catch (Exception e) {
              throw new BadRequestException("Member not found with id: " + memberId);
            }
          })
          .toList();

      // 削除するメンバーを特定
      List<Member> membersToRemove = currentMembers.stream()
          .filter(member -> !newMembers.contains(member))
          .toList();

      // 追加するメンバーを特定
      List<Member> membersToAdd = newMembers.stream()
          .filter(member -> !currentMembers.contains(member))
          .toList();

      // メンバーの更新を実行
      Team updatedTeam = team;
      for (Member member : membersToRemove) {
        updatedTeam = teamOrchestrationService.removeMemberFromTeam(teamId, member);
      }
      for (Member member : membersToAdd) {
        updatedTeam = teamOrchestrationService.addMemberToTeam(teamId, member);
      }

      return TeamResponse.from(updatedTeam);
    } catch (BadRequestException e) {
      throw e;
    }
  }
}