package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.TeamId;
import com.ddd.praha.domain.Member;
import com.ddd.praha.application.repository.TeamRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public List<Team> findAll() {
        return teamMapper.findAll();
    }

    @Override
    public Optional<Team> findById(TeamId id) {
        Team team = teamMapper.findById(id.value());
        return Optional.ofNullable(team);
    }

    @Override
    public Team save(Team team) {
        if (teamMapper.exists(team.getId().value())) {
            // 既存のチームを更新
            teamMapper.update(
                team.getId().value(),
                team.getName().value()
            );
            
            // チームメンバーを更新するために、一度全て削除して再登録
            teamMapper.removeAllMembers(team.getId().value());
        } else {
            // 新しいチームを登録
            teamMapper.insert(
                team.getId().value(),
                team.getName().value()
            );
        }
        
        // チームメンバーを登録
        for (Member member : team.getMembers()) {
            teamMapper.addMember(team.getId().value(), member.getId().value());
        }
        
        return team;
    }

    @Override
    public void delete(Team team) {
        // まずチームメンバーの関連を削除
        teamMapper.removeAllMembers(team.getId().value());
        // チーム自体を削除
        teamMapper.delete(team.getId().value());
    }
}