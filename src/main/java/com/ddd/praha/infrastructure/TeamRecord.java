package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.Team;
import com.ddd.praha.domain.model.TeamId;
import com.ddd.praha.domain.model.TeamName;
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
        List<Member> domainMembers = members != null ?
            members.stream().map(MemberRecord::toMember).toList() :
            List.of();

        return new Team(
            new TeamId(id),
            new TeamName(name),
            domainMembers
        );
    }
}