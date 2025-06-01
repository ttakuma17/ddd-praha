package com.ddd.praha.application.service;

import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.TeamId;
import com.ddd.praha.application.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * チームサービス
 */
@Service
public class TeamService {
    private final TeamRepository teamRepository;
    
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }
    
    /**
     * 全てのチームを取得する
     * @return チームのリスト
     */
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
    
    /**
     * IDでチームを検索する
     * @param id チームID
     * @return チーム（存在しない場合はEmpty）
     */
    public Optional<Team> getTeamById(TeamId id) {
        return teamRepository.findById(id);
    }
    
    /**
     * チームにメンバーを追加する
     * @param teamId チームID
     * @param member 追加するメンバー
     * @return 更新されたチーム
     * @throws IllegalArgumentException チームが存在しない場合
     */
    public Team addMemberToTeam(TeamId teamId, Member member) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("指定されたIDのチームが見つかりません"));
        
        team.addMember(member);
        return teamRepository.save(team);
    }
    
    /**
     * チームからメンバーを削除する
     * @param teamId チームID
     * @param member 削除するメンバー
     * @return 更新されたチーム
     * @throws IllegalArgumentException チームが存在しない場合
     */
    public Team removeMemberFromTeam(TeamId teamId, Member member) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("指定されたIDのチームが見つかりません"));
        
        team.deleteMember(member);
        return teamRepository.save(team);
    }
}