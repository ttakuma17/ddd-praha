package com.ddd.praha.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * チーム編成結果を表す値オブジェクト
 */
public class TeamComposition {
    
    public enum CompositionType {
        NO_CHANGE,  // 変更なし
        SPLIT,      // チーム分割
        MERGE       // チーム合流
    }
    
    private final CompositionType type;
    private final Team originalTeam;
    private final Team newTeam;
    private final List<Member> movedMembers;
    
    private TeamComposition(CompositionType type, Team originalTeam, Team newTeam, List<Member> movedMembers) {
        this.type = type;
        this.originalTeam = originalTeam;
        this.newTeam = newTeam;
        this.movedMembers = new ArrayList<>(movedMembers);
    }
    
    /**
     * 変更なしの結果を作成
     */
    public static TeamComposition noChange(Team team) {
        return new TeamComposition(CompositionType.NO_CHANGE, team, null, new ArrayList<>());
    }
    
    /**
     * チーム分割の結果を作成
     */
    public static TeamComposition split(Team originalTeam, Team newTeam, List<Member> movedMembers) {
        return new TeamComposition(CompositionType.SPLIT, originalTeam, newTeam, movedMembers);
    }
    
    /**
     * チーム合流の結果を作成
     */
    public static TeamComposition merge(Team targetTeam, List<Member> movedMembers) {
        return new TeamComposition(CompositionType.MERGE, targetTeam, null, movedMembers);
    }
    
    public CompositionType getType() {
        return type;
    }
    
    public Team getOriginalTeam() {
        return originalTeam;
    }
    
    public Team getNewTeam() {
        return newTeam;
    }
    
    public List<Member> getMovedMembers() {
        return new ArrayList<>(movedMembers);
    }
}