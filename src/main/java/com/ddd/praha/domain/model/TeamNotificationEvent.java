package com.ddd.praha.domain.model;

import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.Team;

/**
 * チーム関連のドメインイベント
 */
public class TeamNotificationEvent {
    
    public enum NotificationType {
        TEAM_SPLIT,          // チーム分割
        TEAM_MERGED,         // チーム合流
        MONITORING_REQUIRED, // 監視必要
        MERGE_FAILURE        // 合流失敗
    }
    
    private final NotificationType type;
    private final String message;
    private final Team team;
    private final Team relatedTeam;
    private final Member member;
    
    private TeamNotificationEvent(NotificationType type, String message, Team team, Team relatedTeam, Member member) {
        this.type = type;
        this.message = message;
        this.team = team;
        this.relatedTeam = relatedTeam;
        this.member = member;
    }
    
    /**
     * チーム分割イベントを作成
     */
    public static TeamNotificationEvent teamSplit(Team originalTeam, Team newTeam) {
        String message = String.format("チーム '%s' が分割されました。新チーム: '%s'", 
            originalTeam.getName().value(), newTeam.getName().value());
        return new TeamNotificationEvent(NotificationType.TEAM_SPLIT, message, originalTeam, newTeam, null);
    }
    
    /**
     * チーム合流イベントを作成
     */
    public static TeamNotificationEvent teamMerged(Team targetTeam, Member movedMember) {
        String message = String.format("メンバー '%s' がチーム '%s' に合流しました", 
            movedMember.getName().value(), targetTeam.getName().value());
        return new TeamNotificationEvent(NotificationType.TEAM_MERGED, message, targetTeam, null, movedMember);
    }
    
    /**
     * 監視必要イベントを作成
     */
    public static TeamNotificationEvent monitoringRequired(Team team, Member removedMember) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("チームが2名以下になりました。早急な対応が必要です。\n");
        messageBuilder.append("削除されたメンバー: ").append(removedMember.getName().value())
                     .append(" (ID: ").append(removedMember.getId().value())
                     .append(", Email: ").append(removedMember.getEmail().value()).append(")\n");
        messageBuilder.append("チーム名: ").append(team.getName().value()).append("\n");
        messageBuilder.append("現在のチームメンバー:\n");
        
        for (Member currentMember : team.getMembers()) {
            messageBuilder.append("  - ").append(currentMember.getName().value())
                         .append(" (ID: ").append(currentMember.getId().value())
                         .append(", Email: ").append(currentMember.getEmail().value()).append(")\n");
        }
        
        return new TeamNotificationEvent(NotificationType.MONITORING_REQUIRED, messageBuilder.toString(), team, null, removedMember);
    }
    
    /**
     * 合流失敗イベントを作成
     */
    public static TeamNotificationEvent mergeFailure(Team team, Member member) {
        String message = String.format(
            "合流先チームが見つかりません。チーム: %s、合流待ちメンバー: %s (ID: %s、Email: %s)",
            team.getName().value(),
            member.getName().value(),
            member.getId().value(),
            member.getEmail().value()
        );
        return new TeamNotificationEvent(NotificationType.MERGE_FAILURE, message, team, null, member);
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Team getTeam() {
        return team;
    }
    
    public Team getRelatedTeam() {
        return relatedTeam;
    }
    
    public Member getMember() {
        return member;
    }
}