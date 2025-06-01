package com.ddd.praha.application.repository;

import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.TeamId;

import java.util.List;
import java.util.Optional;

/**
 * チームリポジトリインターフェース
 */
public interface TeamRepository {
    /**
     * 全てのチームを取得する
     * @return チームのリスト
     */
    List<Team> findAll();
    
    /**
     * IDでチームを検索する
     * @param id チームID
     * @return チーム（存在しない場合はEmpty）
     */
    Optional<Team> findById(TeamId id);
    
    /**
     * チームを保存する（新規追加または更新）
     * @param team 保存するチーム
     * @return 保存されたチーム
     */
    Team save(Team team);
}