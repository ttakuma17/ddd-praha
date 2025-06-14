package com.ddd.praha.application.service;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.TeamComposition;
import com.ddd.praha.domain.TeamCompositionResult;
import com.ddd.praha.domain.TeamRedistributionResult;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TeamCompositionDomainService {
  /**
   * チーム編成のビジネスルールを実行
   */
  public TeamCompositionResult executeComposition(Team team, Member member, List<Team> allTeams) {
    TeamComposition composition = team.addMemberWithComposition(member);
    return TeamCompositionResult.normal(composition);
  }

  /**
   * チーム再編成のビジネスルールを実行
   */
  public TeamRedistributionResult executeRedistribution(Team team, Member removedMember, List<Team> allTeams) {
    team.deleteMember(removedMember);
    boolean needsMonitoring = team.needsMonitoring();

    Optional<TeamComposition> compositionOpt = team.mergeWithOtherTeam(allTeams);

    if (compositionOpt.isPresent()) {
      TeamComposition composition = compositionOpt.get();
      if (needsMonitoring) {
        return TeamRedistributionResult.needsMonitoringAndMerge(composition, removedMember);
      }
      return TeamRedistributionResult.merged(composition, removedMember);
    }

    // 合流先が見つからない場合
    TeamComposition noChangeComposition = TeamComposition.noChange(team);
    if (needsMonitoring) {
      return TeamRedistributionResult.needsMonitoring(noChangeComposition, removedMember);
    }

    return TeamRedistributionResult.normal(noChangeComposition, removedMember);

  }
}
