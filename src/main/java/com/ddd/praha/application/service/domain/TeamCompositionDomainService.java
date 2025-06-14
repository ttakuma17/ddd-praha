package com.ddd.praha.application.service.domain;

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
  public TeamCompositionResult executeComposition(Team team, Member member) {
    TeamComposition composition = team.addMemberWithComposition(member);
    return TeamCompositionResult.normal(composition);
  }

  /**
   * チーム再編成のビジネスルールを実行
   */
  public TeamRedistributionResult executeRedistribution(Team team, Member removedMember, List<Team> allTeams) {
    team.deleteMember(removedMember);
    boolean needsMonitoring = team.needsMonitoring();
    boolean needsRedistribution = team.needsRedistribution();

    // チームが1名になった場合、合流を試みる
    if (needsRedistribution) {
      Optional<TeamComposition> compositionOpt = team.mergeWithOtherTeam(allTeams);
      
      if (compositionOpt.isPresent()) {
        TeamComposition composition = compositionOpt.get();
        if (needsMonitoring) {
          return TeamRedistributionResult.needsMonitoringAndMerge(composition, removedMember);
        }
        return TeamRedistributionResult.merged(composition, removedMember);
      } else {
        // 合流先が見つからない場合
        TeamComposition noChangeComposition = TeamComposition.noChange(team);
        return TeamRedistributionResult.mergeFailure(noChangeComposition, team.getMembers().getFirst());
      }
    }

    // 合流が不要な場合
    TeamComposition noChangeComposition = TeamComposition.noChange(team);
    if (needsMonitoring) {
      return TeamRedistributionResult.needsMonitoring(noChangeComposition, removedMember);
    }

    return TeamRedistributionResult.normal(noChangeComposition, removedMember);
  }

  /**
   * メンバーを最適なチームに割り当てる（復帰時）
   */
  public TeamCompositionResult assignMemberToTeam(Member member, List<Team> allTeams) {
    Optional<Team> targetTeam = Team.findSmallestTeam(allTeams);
    
    if (targetTeam.isEmpty()) {
      throw new IllegalStateException("メンバーを割り当て可能なチームが見つかりません");
    }
    
    TeamComposition composition = targetTeam.get().addMemberWithComposition(member);
    
    // チーム分割が発生したかどうかを確認
    if (composition.getType() == TeamComposition.CompositionType.SPLIT) {
      return TeamCompositionResult.split(composition);
    }
    
    return TeamCompositionResult.normal(composition);
  }
}