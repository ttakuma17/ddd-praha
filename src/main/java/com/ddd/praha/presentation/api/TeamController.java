package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.MemberService;
import com.ddd.praha.application.service.TeamQueryService;
import com.ddd.praha.application.service.TeamOrchestrationService;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.TeamId;
import com.ddd.praha.presentation.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * チームコントローラー
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
     * @return チームのリスト
     */
    @GetMapping
    public List<TeamResponse> getAllTeams() {
        List<Team> teams = teamQueryService.getAllTeams();
        return teams.stream()
                .map(TeamResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * IDでチームを検索する
     * @param id チームID
     * @return チーム情報
     */
    @GetMapping("/{id}")
    public TeamResponse findTeamById(@PathVariable String id) {
        return teamQueryService.findTeamById(new TeamId(id))
                .map(TeamResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + id));
    }
    
    /**
     * チームのメンバーを更新する
     * @param id チームID
     * @param request メンバー更新リクエスト
     * @return 更新されたチーム情報
     */
    @PutMapping("/{id}/members")
    public TeamResponse updateTeamMembers(
            @PathVariable String id,
            @RequestBody TeamMemberUpdateRequest request) {
        // チームを取得
        Optional<Team> teamOptional = teamQueryService.findTeamById(new TeamId(id));
        if (teamOptional.isEmpty()) {
            throw new ResourceNotFoundException("Team not found: " + id);
        }
        Team team = teamOptional.get();
        
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