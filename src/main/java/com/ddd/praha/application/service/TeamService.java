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

        if (team.needsRedistribution()){
            // 参加者が増えることでチームが5名になってしまう場合、自動的に2つのチームに分解する必要がある
            // 分解する仕様の指定はないので、新しいチームはランダムに選択して構わない
        }

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

        if (team.needsRedistribution()){
            // - 合流先は最も参加人数が少ないチームから自動的に選ばれる
            // - 参加人数が同じの場合はランダムに選択する
            // - 課題の簡略化のため、合流先の参加者には合流の許諾を得る必要なく、アプリケーションが自動的に（勝手に）チームを組み替えることとする

            // もし合流可能なチームがない場合は、その旨を管理者にメールして連絡する。その際メール文を見れば「どの参加者が減ったのか」「どの参加者が合流先を探しているのか」が分かるようにしてください
            // 合流可能なチームがない場合は、RabbitMQのキューにメッセージを格納する
        }

        team.deleteMember(member);
        return teamRepository.save(team);
    }
}