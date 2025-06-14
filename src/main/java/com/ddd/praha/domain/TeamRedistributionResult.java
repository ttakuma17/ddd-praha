package com.ddd.praha.domain;

/**
 * チーム再編成の結果を表現するクラス
 */
public record TeamRedistributionResult(
    TeamComposition composition,
    Member removedMember,
    boolean requiresMonitoring,
    boolean requiresMerge
) {
  /**
   * 通常の削除結果を作成
   */
  public static TeamRedistributionResult normal(TeamComposition composition, Member removedMember) {
    return new TeamRedistributionResult(composition, removedMember, false, false);
  }

  /**
   * 監視が必要な結果を作成
   */
  public static TeamRedistributionResult needsMonitoring(TeamComposition composition, Member removedMember) {
    return new TeamRedistributionResult(composition, removedMember, true, false);
  }

  /**
   * チーム合流の結果を作成
   */
  public static TeamRedistributionResult merged(TeamComposition composition, Member removedMember) {
    return new TeamRedistributionResult(composition, removedMember, false, true);
  }

  /**
   * 監視が必要で合流も必要な結果を作成
   */
  public static TeamRedistributionResult needsMonitoringAndMerge(
      TeamComposition composition, Member removedMember) {
    return new TeamRedistributionResult(composition, removedMember, true, true);
  }
}
