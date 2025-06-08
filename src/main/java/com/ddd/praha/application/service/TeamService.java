package com.ddd.praha.application.service;

import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.TeamId;
import com.ddd.praha.domain.TeamName;
import com.ddd.praha.application.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * チームサービス
 */
@Service
public class TeamService {
    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);
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
        
        if (team.needsSplitting()) {
            // 参加者が増えることでチームが5名になってしまう場合、自動的に2つのチームに分解する必要がある
            // 分解する仕様の指定はないので、新しいチームはランダムに選択して構わない
            return splitTeamIntoTwo(team);
        }

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

        if (team.needsMonitoring()){
            // 参加者が減ることでチームが2名以下になってしまう場合、状況を早期に検知して運営側が解決できるよう管理者のメールアドレス（今回の課題ではあなたのメールアドレス）宛にメールを送信する。
            // その際メール文面を見れば「どの参加者が減ったのか」「どのチームが2名以下になったのか」「そのチームの現在の参加者名」が分かるようにする
            sendMonitoringNotification(team, member);
        }

        
        if (team.needsRedistribution()) {
            // - 合流先は最も参加人数が少ないチームから自動的に選ばれる
            // - 参加人数が同じの場合はランダムに選択する
            // - 課題の簡略化のため、合流先の参加者には合流の許諾を得る必要なく、アプリケーションが自動的に（勝手に）チームを組み替えることとする

            // もし合流可能なチームがない場合は、その旨を管理者にメールして連絡する。その際メール文を見れば「どの参加者が減ったのか」「どの参加者が合流先を探しているのか」が分かるようにしてください
            // 合流可能なチームがない場合は、RabbitMQのキューにメッセージを格納する
            return mergeWithOtherTeam(team);
        }
        
        return teamRepository.save(team);
    }
    
    /**
     * チームを2つに分割する
     * @param team 分割対象のチーム
     * @return 分割後の元チーム
     */
    private Team splitTeamIntoTwo(Team team) {
        List<Member> members = team.getMembers();
        int half = members.size() / 2;
        
        // 前半のメンバーは元のチームに残す
        List<Member> firstHalf = new ArrayList<>(members.subList(0, half));
        // 後半のメンバーで新しいチームを作成
        List<Member> secondHalf = new ArrayList<>(members.subList(half, members.size()));
        
        // 新しいチームを作成
        TeamName newTeamName = new TeamName(team.getName().value() + "-分割");
        Team newTeam = new Team(newTeamName, secondHalf);
        teamRepository.save(newTeam);
        
        // 元のチームから後半のメンバーを削除
        for (Member memberToRemove : secondHalf) {
            team.deleteMember(memberToRemove);
        }
        
        logger.info("チーム {} を2つに分割しました。新チーム: {}", team.getName().value(), newTeam.getName().value());
        
        return teamRepository.save(team);
    }
    
    /**
     * 他のチームと合流する
     * @param team 合流元のチーム（1名のチーム）
     * @return 合流先のチーム、または合流できない場合は元のチーム
     */
    private Team mergeWithOtherTeam(Team team) {
        List<Team> allTeams = teamRepository.findAll();
        
        // 合流先候補チームを探す（自分以外で4名以下のチーム）
        Optional<Team> targetTeam = allTeams.stream()
            .filter(t -> !t.getId().equals(team.getId()))
            .filter(t -> t.getMembers().size() < 4)
            .min(Comparator.comparingInt(t -> t.getMembers().size()));
        
        if (targetTeam.isPresent()) {
            Team mergeTarget = targetTeam.get();
            Member remainingMember = team.getMembers().get(0);
            
            // 合流先チームにメンバーを追加
            mergeTarget.addMember(remainingMember);
            teamRepository.save(mergeTarget);
            
            logger.info("チーム {} のメンバー {} をチーム {} に合流させました", 
                team.getName().value(), 
                remainingMember.getName().value(), 
                mergeTarget.getName().value());
            
            return mergeTarget;
        } else {
            // 合流可能なチームがない場合
            Member remainingMember = team.getMembers().get(0);
            String message = String.format(
                "合流先チームが見つかりません。チーム: %s、合流待ちメンバー: %s (ID: %s、Email: %s)",
                team.getName().value(),
                remainingMember.getName().value(),
                remainingMember.getId().value(),
                remainingMember.getEmail().value()
            );
            
            logger.warn(message);
            // TODO: RabbitMQのキューにメッセージを送信する
            sendMergeNotification(message);
            
            return team;
        }
    }
    
    /**
     * 合流通知を送信する（現在はログ出力、将来的にRabbitMQに変更予定）
     * @param message 通知メッセージ
     */
    private void sendMergeNotification(String message) {
        // TODO: RabbitMQのキューにメッセージを格納する実装に変更する
        logger.error("管理者通知: {}", message);
    }
    
    /**
     * チーム監視通知を送信する（チームが2名以下になった場合）
     * @param team 監視対象のチーム
     * @param removedMember 削除されたメンバー
     */
    private void sendMonitoringNotification(Team team, Member removedMember) {
        StringBuilder message = new StringBuilder();
        message.append("チームが2名以下になりました。早急な対応が必要です。\n");
        message.append("削除されたメンバー: ").append(removedMember.getName().value())
               .append(" (ID: ").append(removedMember.getId().value())
               .append(", Email: ").append(removedMember.getEmail().value()).append(")\n");
        message.append("チーム名: ").append(team.getName().value()).append("\n");
        message.append("現在のチームメンバー:\n");
        
        for (Member currentMember : team.getMembers()) {
            message.append("  - ").append(currentMember.getName().value())
                   .append(" (ID: ").append(currentMember.getId().value())
                   .append(", Email: ").append(currentMember.getEmail().value()).append(")\n");
        }
        
        // TODO: 実際のメール送信実装に変更する
        logger.warn("管理者メール通知: {}", message);
    }
}