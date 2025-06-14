package com.ddd.praha.application.service.usecase;

import com.ddd.praha.application.repository.NotificationRepository;
import com.ddd.praha.application.exception.NotificationException;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.TeamNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

  public NotificationService(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  /**
   * チーム分割の通知を送信
   * @param originalTeam 元のチーム
   * @param newTeam 新しく作成されたチーム
   */
  public void notifyTeamSplit(Team originalTeam, Team newTeam) {
    TeamNotificationEvent event = TeamNotificationEvent.teamSplit(originalTeam, newTeam);
    sendNotification(event);
    logger.info("チーム {} を2つに分割しました。新チーム: {}",
        originalTeam.getName().value(),
        newTeam.getName().value());
  }

  /**
   * チーム合流の通知を送信
   * @param mergedTeam 合流先のチーム
   * @param movedMember 移動したメンバー
   */
  public void notifyTeamMerge(Team mergedTeam, Member movedMember) {
    TeamNotificationEvent event = TeamNotificationEvent.teamMerged(mergedTeam, movedMember);
    sendNotification(event);
    logger.info("メンバー {} をチーム {} に合流させました",
        movedMember.getName().value(),
        mergedTeam.getName().value());
  }

  /**
   * チーム監視必要の通知を送信
   * @param team 監視が必要なチーム
   * @param removedMember 削除されたメンバー
   */
  public void notifyTeamMonitoring(Team team, Member removedMember) {
    TeamNotificationEvent event = TeamNotificationEvent.monitoringRequired(team, removedMember);
    sendNotification(event);
    logger.warn("チーム {} が2名以下になりました。監視が必要です。削除されたメンバー: {}",
        team.getName().value(),
        removedMember.getName().value());
  }

  /**
   * チーム合流失敗の通知を送信
   * @param team 合流に失敗したチーム
   * @param member 合流できなかったメンバー
   */
  public void notifyMergeFailure(Team team, Member member) {
    TeamNotificationEvent event = TeamNotificationEvent.mergeFailure(team, member);
    sendNotification(event);
    logger.error("チーム {} の合流先が見つかりませんでした。メンバー: {}",
        team.getName().value(),
        member.getName().value());
  }

  /**
   * 通知を送信
   * @param event 送信するイベント
   */
  private void sendNotification(TeamNotificationEvent event) {
    try {
      notificationRepository.sendNotification(event);
    } catch (Exception e) {
      logger.error("通知の送信に失敗しました: {}", event.getMessage(), e);
      throw new NotificationException("通知の送信に失敗しました", e);
    }
  }
}
