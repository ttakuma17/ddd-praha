package com.ddd.praha.application.service.domain;

import com.ddd.praha.domain.entity.*;
import com.ddd.praha.domain.model.*;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * チーム編成のドメインサービス。
 * 
 * <p>チームメンバーの追加・削除に関する複雑なビジネスルールを実装する。
 * プラハチャレンジの学習効果を最大化するため、適切なチーム構成を維持する責務を持つ。</p>
 * 
 * <p>主なビジネスルール：</p>
 * <ul>
 *   <li>チーム人数は2〜4名が適正範囲</li>
 *   <li>5名以上になった場合は自動的に2つのチームに分割</li>
 *   <li>1名になった場合は他チームとの合流を試行</li>
 *   <li>2名以下の場合は監視対象として通知</li>
 *   <li>復帰メンバーは最小人数のチームに優先割り当て</li>
 * </ul>
 * 
 * <p>このサービスはチームエンティティのビジネスロジックを補完し、
 * 複数チーム間の協調処理を担当する。</p>
 */
@Service
public class TeamCompositionDomainService {
  /**
   * 指定されたチームにメンバーを追加し、編成結果を返す。
   * 
   * <p>メンバー追加時のビジネスルールを適用し、
   * チーム分割が必要かどうかを判定する。</p>
   * 
   * @param team メンバーを追加するチーム
   * @param member 追加するメンバー
   * @return チーム編成結果（分割の有無を含む）
   */
  public TeamCompositionResult executeComposition(Team team, Member member) {
    TeamComposition composition = team.addMemberWithComposition(member);
    return TeamCompositionResult.normal(composition);
  }

  /**
   * チームからメンバーを削除し、必要に応じて再編成を実行する。
   * 
   * <p>メンバー削除後のチーム状態を評価し、以下の処理を行う：</p>
   * <ul>
   *   <li>1名になった場合：他チームとの合流を試行</li>
   *   <li>2名以下の場合：監視対象として記録</li>
   *   <li>合流先がない場合：合流失敗として処理</li>
   * </ul>
   * 
   * @param team メンバーを削除するチーム
   * @param removedMember 削除されるメンバー
   * @param allTeams 合流先候補となる全チーム
   * @return チーム再編成結果（監視・合流・失敗の状態を含む）
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
   * 復帰したメンバーを最適なチームに割り当てる。
   * 
   * <p>全チームの中から最も人数が少ないチームを選択し、
   * メンバーを追加する。追加後にチーム分割が必要な場合は
   * 自動的に実行される。</p>
   * 
   * @param member 復帰するメンバー
   * @param allTeams 割り当て候補となる全チーム
   * @return チーム編成結果（分割の有無を含む）
   */
  public TeamCompositionResult assignMemberToTeam(Member member, List<Team> allTeams) {
    Team targetTeam = Team.findSmallestTeam(allTeams);
    
    TeamComposition composition = targetTeam.addMemberWithComposition(member);
    
    // チーム分割が発生したかどうかを確認
    if (composition.getType() == TeamComposition.CompositionType.SPLIT) {
      return TeamCompositionResult.split(composition);
    }
    
    return TeamCompositionResult.normal(composition);
  }
}