package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.usecase.MemberService;
import com.ddd.praha.application.service.usecase.TeamQueryService;
import com.ddd.praha.application.service.usecase.TeamOrchestrationService;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.TeamId;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    // 現在のメンバーを取得
    List<Member> currentMembers = new ArrayList<>(team.getMembers());

    // リクエストで指定されたメンバーを取得
    List<Member> newMembers = new ArrayList<>();
    for (String memberId : request.getMemberIds()) {
      Member member = memberService.get(new MemberId(memberId));
      newMembers.add(member);
    }

    // 削除するメンバーを特定して削除
    for (Member member : currentMembers) {
      if (!newMembers.contains(member)) {
        team = teamOrchestrationService.removeMemberFromTeam(team.getId(), member);
      }
    }

    // 追加するメンバーを特定して追加
    for (Member member : newMembers) {
      if (!currentMembers.contains(member)) {
        team = teamOrchestrationService.addMemberToTeam(team.getId(), member);
      }
    }

    return TeamResponse.from(team);
  }
}