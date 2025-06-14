package com.ddd.praha.domain;

/**
 * チーム編成の結果を表現するクラス
 */
public record TeamCompositionResult(
    TeamComposition composition,
    boolean requiresSplit
) {
  /**
   * 通常の追加結果を作成
   */
  public static TeamCompositionResult normal(TeamComposition composition) {
    return new TeamCompositionResult(composition, false);
  }

  /**
   * チーム分割結果を作成
   */
  public static TeamCompositionResult split(TeamComposition composition) {
    return new TeamCompositionResult(composition, true);
  }
}
