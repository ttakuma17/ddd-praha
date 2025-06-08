package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.MemberService;
import com.ddd.praha.application.service.TeamService;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.TeamId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final TeamService teamService;
    private final MemberService memberService;
    
    public TeamController(TeamService teamService, MemberService memberService) {
        this.teamService = teamService;
        this.memberService = memberService;
    }
    
    /**
     * 全てのチームを取得する
     * @return チームのリスト
     */
    @GetMapping
    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        List<Team> teams = teamService.getAllTeams();
        List<TeamResponse> response = teams.stream()
                .map(TeamResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * IDでチームを検索する
     * @param id チームID
     * @return チーム情報
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable String id) {
        return teamService.getTeamById(new TeamId(id))
                .map(team -> ResponseEntity.ok(TeamResponse.fromDomain(team)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * チームのメンバーを更新する
     * @param id チームID
     * @param request メンバー更新リクエスト
     * @return 更新されたチーム情報
     */
    @PutMapping("/{id}/members")
    public ResponseEntity<TeamResponse> updateTeamMembers(
            @PathVariable String id,
            @RequestBody TeamMemberUpdateRequest request) {
        try {
            // チームを取得
            Optional<Team> teamOptional = teamService.getTeamById(new TeamId(id));
            if (teamOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Team team = teamOptional.get();
            
            // 現在のメンバーを取得
            List<Member> currentMembers = new ArrayList<>(team.getMembers());
            
            // リクエストで指定されたメンバーを取得
            List<Member> newMembers = new ArrayList<>();
            for (String memberId : request.getMemberIds()) {
                Optional<Member> memberOptional = memberService.getMemberById(new MemberId(memberId));
                if (memberOptional.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }
                newMembers.add(memberOptional.get());
            }
            
            // 削除するメンバーを特定して削除
            for (Member member : currentMembers) {
                if (!newMembers.contains(member)) {
                    team = teamService.removeMemberFromTeam(team.getId(), member);
                }
            }
            
            // 追加するメンバーを特定して追加
            for (Member member : newMembers) {
                if (!currentMembers.contains(member)) {
                    team = teamService.addMemberToTeam(team.getId(), member);
                }
            }
            
            return ResponseEntity.ok(TeamResponse.fromDomain(team));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}