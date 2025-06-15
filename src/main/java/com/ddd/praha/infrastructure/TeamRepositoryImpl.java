package com.ddd.praha.infrastructure;


import com.ddd.praha.application.repository.TeamRepository;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.Team;
import com.ddd.praha.domain.model.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * チームリポジトリのMyBatis実装
 */
@Repository
public class TeamRepositoryImpl implements TeamRepository {
    private final TeamMapper teamMapper;

    public TeamRepositoryImpl(TeamMapper teamMapper) {
        this.teamMapper = teamMapper;
    }

    @Override
    public List<Team> getAll() {
        List<TeamMemberJoinRecord> joinRecords = teamMapper.getAllWithMembers();
        return convertJoinRecordsToTeams(joinRecords);
    }

    @Override
    public Team get(TeamId id) {
        List<TeamMemberJoinRecord> joinRecords = teamMapper.getWithMembers(id);
        if (joinRecords.isEmpty()) {
            throw new IllegalStateException("Team record is null.");
        }
        List<Team> teams = convertJoinRecordsToTeams(joinRecords);
        return teams.get(0);
    }

    @Override
    public void create(Team team) {
        if (teamMapper.exists(team.getId())) {
            throw new IllegalStateException("チームID " + team.getId().value() + " は既に使用されています");
        }

        // チーム名の重複チェック
        TeamRecord existingTeam = teamMapper.findByName(team.getName().value());
        if (existingTeam != null && !existingTeam.id().equals(team.getId().value())) {
            throw new IllegalArgumentException("このチーム名は既に使用されています");
        }

        teamMapper.insert(team);

        // メンバーIDのリストを抽出
        List<MemberId> memberIds = team.getMembers().stream()
            .map(Member::getId)
            .toList();

        if (!memberIds.isEmpty()) {
            teamMapper.addMembers(team.getId(), memberIds);
        }
    }

    @Override
    public void addMember(TeamId teamId, MemberId memberId) {
        teamMapper.addMember(teamId, memberId);
    }

    @Override
    public void removeMember(TeamId teamId, MemberId memberId) {
        teamMapper.removeMember(teamId, memberId);
    }

    @Override
    public void delete(Team team) {
        // まずチームメンバーの関連を削除
        teamMapper.removeAllMembers(team.getId());
        // チーム自体を削除
        teamMapper.delete(team.getId());
    }

    private List<Team> convertJoinRecordsToTeams(List<TeamMemberJoinRecord> joinRecords) {
        Map<String, List<TeamMemberJoinRecord>> teamGroups = joinRecords.stream()
            .collect(Collectors.groupingBy(TeamMemberJoinRecord::teamId));

        return teamGroups.entrySet().stream()
            .map(entry -> {
                String teamId = entry.getKey();
                List<TeamMemberJoinRecord> records = entry.getValue();
                
                // チーム情報は全レコードで同じなので最初のレコードから取得
                TeamMemberJoinRecord firstRecord = records.get(0);
                
                // メンバーリストを構築（memberId がnullでないもののみ）
                List<Member> members = records.stream()
                    .filter(record -> record.memberId() != null && record.memberStatus() != null)
                    .map(record -> {
                        try {
                            return new Member(
                                new MemberId(record.memberId()),
                                new MemberName(record.memberName()),
                                new Email(record.memberEmail()),
                                EnrollmentStatus.valueOf(record.memberStatus())
                            );
                        } catch (IllegalArgumentException e) {
                            throw new IllegalStateException("Invalid member status: " + record.memberStatus(), e);
                        }
                    })
                    .toList();

                return new Team(
                    new TeamId(teamId),
                    new TeamName(firstRecord.teamName()),
                    members
                );
            })
            .toList();
    }
}