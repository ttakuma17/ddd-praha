package com.ddd.praha.infrastructure;


import com.ddd.praha.application.repository.TeamRepository;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.Team;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.model.TeamId;
import org.springframework.stereotype.Repository;

import java.util.List;

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
        List<TeamRecord> all = teamMapper.getAll();
        return all.stream().map(TeamRecord::toTeam).toList();
    }

    @Override
    public Team get(TeamId id) {
        TeamRecord team = teamMapper.get(id);
        if (team == null) {
            throw new IllegalStateException("Team record is null.");
        }
        return team.toTeam();
    }

    @Override
    public void create(Team team) {
        if (teamMapper.exists(team.getId())) {
            throw new IllegalStateException("チームID " + team.getId().value() + " は既に使用されています");
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
}