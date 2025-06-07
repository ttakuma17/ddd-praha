package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.TeamId;
import com.ddd.praha.domain.TeamName;
import com.ddd.praha.domain.Member;
import java.lang.reflect.Field;
import java.util.List;

/**
 * チームのSQLマッピングレコード
 */
public record TeamRecord(
    String id,
    String name,
    List<MemberRecord> members
) {

    /**
     * ドメインのTeamオブジェクトに変換する
     * @return Team
     */
    public Team toTeam() {
        try {
            // MemberRecordをMemberに変換
            List<Member> domainMembers = members != null ? 
                members.stream().map(MemberRecord::toMember).toList() : 
                List.of();
            
            // 通常のコンストラクタでTeamを作成
            Team team = new Team(
                new TeamName(name),
                domainMembers
            );
            
            // リフレクションを使用してIDフィールドを設定
            Field idField = Team.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(team, new TeamId(id));
            
            return team;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Team domain object", e);
        }
    }
}