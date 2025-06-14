package com.ddd.praha.application.repository;

import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.TeamId;

import java.util.List;

/**
 * チームリポジトリインターフェース
 */
public interface TeamRepository {
    /**
     * 全てのチームを取得する
     * @return チームのリスト
     */
    List<Team> getAll();
    
    /**
     * IDでチームを検索する
     * @param id チームID
     * @return チーム（存在しない場合はEmpty）
     */
    Team get(TeamId id);


    /**
     * チームを保存する（新規追加または更新）
     * @param team 保存するチーム
     */
    void create(Team team);

    /**
     * チームにメンバーを追加する
     * @param teamId チームID
     * @param memberId メンバーID
     */
    void addMember(TeamId teamId, MemberId memberId);

    /**
     * チームからメンバーを削除する
     * @param teamId チームID
     * @param memberId メンバーID
     */
    void removeMember(TeamId teamId, MemberId memberId);

    /**
     * チームを削除する
     * @param team 削除するチーム
     */
    void delete(Team team);
}