package com.ddd.praha.application.service.usecase;

import com.ddd.praha.application.repository.TeamRepository;
import com.ddd.praha.domain.entity.*;
import com.ddd.praha.domain.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<Team> getAll() {
        return teamRepository.getAll();
    }
    
    /**
     * IDでチームを検索する
     * @param id チームID
     */
    public Team get(TeamId id) {
        return teamRepository.get(id);
    }
}