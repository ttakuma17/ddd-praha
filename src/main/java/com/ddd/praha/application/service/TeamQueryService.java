package com.ddd.praha.application.service;

import com.ddd.praha.application.repository.TeamRepository;
import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.TeamId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * チーム照会専用サービス
 */
@Service
public class TeamQueryService {
    
    private final TeamRepository teamRepository;
    
    public TeamQueryService(TeamRepository teamRepository) {
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
    public Optional<Team> findTeamById(TeamId id) {
        return teamRepository.findById(id);
    }
}