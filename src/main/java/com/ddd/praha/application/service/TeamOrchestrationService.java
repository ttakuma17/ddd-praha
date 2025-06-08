package com.ddd.praha.application.service;

import com.ddd.praha.application.repository.NotificationRepository;
import com.ddd.praha.domain.*;
import com.ddd.praha.application.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * チーム編成のオーケストレーションを担当するアプリケーションサービス
 * ユースケースの調整と通知処理を行う
 */
@Service
@Transactional
public class TeamOrchestrationService {
    private static final Logger logger = LoggerFactory.getLogger(TeamOrchestrationService.class);
    
    private final TeamRepository teamRepository;
    private final NotificationRepository notificationRepository;
    
    public TeamOrchestrationService(TeamRepository teamRepository, 
                                  NotificationRepository notificationRepository) {
        this.teamRepository = teamRepository;
        this.notificationRepository = notificationRepository;
    }
    
    /**
     * チームにメンバーを追加し、必要に応じてチーム編成を行う
     * @param teamId チームID
     * @param member 追加するメンバー
     * @return 処理結果のチーム
     */
    public Team addMemberToTeam(TeamId teamId, Member member) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("指定されたIDのチームが見つかりません"));

        TeamComposition composition = team.addMemberWithComposition(member);
        
        // 編成結果に基づいてリポジトリを更新
        Team resultTeam = teamRepository.save(composition.getOriginalTeam());
        
        if (composition.getType() == TeamComposition.CompositionType.SPLIT) {
            teamRepository.save(composition.getNewTeam());
            
            // 分割通知を送信
            TeamNotificationEvent event = TeamNotificationEvent.teamSplit(
                composition.getOriginalTeam(), composition.getNewTeam());
            notificationRepository.sendNotification(event);
            
            logger.info("チーム {} を2つに分割しました。新チーム: {}", 
                team.getName().value(), composition.getNewTeam().getName().value());
        }
        
        return resultTeam;
    }
    
    /**
     * チームからメンバーを削除し、必要に応じてチーム編成を行う
     * @param teamId チームID
     * @param member 削除するメンバー
     * @return 処理結果のチーム
     */
    public Team removeMemberFromTeam(TeamId teamId, Member member) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("指定されたIDのチームが見つかりません"));

        // 削除前の状態を保存
        boolean willNeedRedistribution = team.getMembers().size() == 2; // 削除後1名になる
        
        team.deleteMember(member);

        // 監視が必要な場合の通知
        if (team.needsMonitoring()) {
            TeamNotificationEvent event = TeamNotificationEvent.monitoringRequired(team, member);
            notificationRepository.sendNotification(event);
        }

        // 合流が必要な場合の処理
        if (willNeedRedistribution) {
            List<Team> allTeams = teamRepository.findAll();
            Optional<TeamComposition> composition = team.mergeWithOtherTeam(allTeams);
            
            if (composition.isPresent()) {
                TeamComposition result = composition.get();
                if (result.getType() == TeamComposition.CompositionType.MERGE) {
                    // 合流先チームを保存
                    Team mergedTeam = teamRepository.save(result.getOriginalTeam());
                    
                    // 元のチームを削除（1名チームは解散）
                    teamRepository.delete(team);
                    
                    // 合流通知を送信
                    TeamNotificationEvent event = TeamNotificationEvent.teamMerged(
                        mergedTeam, result.getMovedMembers().get(0));
                    notificationRepository.sendNotification(event);
                    
                    logger.info("チーム {} のメンバー {} をチーム {} に合流させました", 
                        team.getName().value(), 
                        member.getName().value(), 
                        mergedTeam.getName().value());
                    
                    return mergedTeam;
                }
            } else {
                // 合流失敗の通知
                TeamNotificationEvent event = TeamNotificationEvent.mergeFailure(team, member);
                notificationRepository.sendNotification(event);
            }
        }
        
        return teamRepository.save(team);
    }
}