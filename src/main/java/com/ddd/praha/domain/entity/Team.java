package com.ddd.praha.domain.entity;

import com.ddd.praha.domain.model.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * チーム集約？
 */
public class Team {
  TeamId id;
  TeamName name;
  List<Member> list;

  public TeamId getId() {
    return id;
  }

  public TeamName getName() {
    return name;
  }

  public List<Member> getMembers() {
    return new ArrayList<>(list);
  }

  public Team(TeamId id, TeamName name, List<Member> list) {
    this.id = id;
    this.name = name;
    this.list = new ArrayList<>(list);
  }

  public Team(TeamName name, List<Member> list) {
    validateMembers(list);
    this.id = TeamId.generate();
    this.name = name;
    this.list = new ArrayList<>(list);
  }

  private void validateMembers(List<Member> members) {
    if (members == null || members.isEmpty()) {
      throw new IllegalArgumentException("メンバーリストは必須です");
    }
    if (members.size() > 4) {
      throw new IllegalArgumentException("チームに所属できる人数は5名以上に設定することはできません");
    }
    if (members.size() < 2) {
      throw new IllegalArgumentException("チーム人数は1名にはできません");
    }
  }

  public void addMember(Member member){
    if (!member.canJoin()){
      throw new IllegalArgumentException("在籍中ではない参加者はチームに追加できません");
    }
    if (list.contains(member)) {
      throw new IllegalArgumentException("指定された参加者は既にチームに所属しています");
    }
    list.add(member);
  }

  public void deleteMember(Member member){
    if (!list.contains(member)) {
      throw new IllegalArgumentException("指定された参加者はチームに所属していません");
    }
    list.remove(member);
  }

  public boolean needsMonitoring(){
    return list.size() <= 2;
  }

  public boolean needsRedistribution(){
    return list.size() == 1;
  }

  public boolean needsSplitting(){
    return list.size() >= 5;
  }

  public boolean canAcceptNewMember(){
    return list.size() < 4;
  }

  /**
   * メンバーを追加し、必要に応じてチーム分割を行う
   * @param member 追加するメンバー
   * @return チーム編成結果
   */
  public TeamComposition addMemberWithComposition(Member member) {
    addMember(member);
    
    if (needsSplitting()) {
      return splitTeam();
    }
    
    return TeamComposition.noChange(this);
  }

  /**
   * チームを2つに分割する
   * @return 分割結果
   */
  private TeamComposition splitTeam() {
    List<Member> members = getMembers();
    int half = members.size() / 2;
    
    // 後半のメンバーで新しいチームを作成
    List<Member> movedMembers = new ArrayList<>(members.subList(half, members.size()));
    
    // 新しいチームを作成
    TeamName newTeamName = new TeamName(this.name.value() + "-分割");
    Team newTeam = new Team(newTeamName, movedMembers);
    
    // 元のチームから後半のメンバーを削除
    for (Member memberToRemove : movedMembers) {
      this.deleteMember(memberToRemove);
    }
    
    return TeamComposition.split(this, newTeam, movedMembers);
  }

  /**
   * 他のチームと合流する
   * @param allTeams 全チームのリスト
   * @return 合流結果（合流先が見つからない場合はEmpty）
   */
  public Optional<TeamComposition> mergeWithOtherTeam(List<Team> allTeams) {
    if (list.size() != 1) {
      throw new IllegalStateException("合流は1名のチームのみ可能です");
    }
    
    // 合流先候補チームを探す（自分以外で4名未満のチーム）
    List<Team> candidateTeams = allTeams.stream()
        .filter(t -> !t.getId().equals(this.getId()))
        .filter(Team::canAcceptNewMember)
        .toList();
    
    if (candidateTeams.isEmpty()) {
      return Optional.empty();
    }
    
    // 最小人数を見つける
    int minSize = candidateTeams.stream()
        .mapToInt(t -> t.getMembers().size())
        .min()
        .orElse(Integer.MAX_VALUE);
    
    // 最小人数のチームを全て取得
    List<Team> smallestTeams = candidateTeams.stream()
        .filter(t -> t.getMembers().size() == minSize)
        .toList();
    
    // 同じ人数の場合はランダムに選択
    Random random = new Random();
    Team mergeTarget = smallestTeams.get(random.nextInt(smallestTeams.size()));
    Member memberToMove = this.list.getFirst();
    
    // 合流先チームにメンバーを追加
    mergeTarget.addMember(memberToMove);
    
    return Optional.of(TeamComposition.merge(mergeTarget, List.of(memberToMove)));
  }
  
  /**
   * 最も人数が少ないチームを見つける（復帰時のチーム割り当て用）
   * @param teams チームのリスト
   * @return 最も人数が少ないチーム（同数の場合はランダム）
   */
  public static Team findSmallestTeam(List<Team> teams) {
    if (teams == null || teams.isEmpty()) {
      throw new IllegalArgumentException("チームリストは必須です");
    }
    
    // 4名未満のチーム（合流可能なチーム）を探す
    List<Team> candidateTeams = teams.stream()
        .filter(Team::canAcceptNewMember)
        .toList();
    
    // 合流可能なチームがない場合は最も人数が少ないチームを返す（分割前提）
    if (candidateTeams.isEmpty()) {
      candidateTeams = teams;
    }
    
    // 最小人数を見つける
    int minSize = candidateTeams.stream()
        .mapToInt(t -> t.getMembers().size())
        .min()
        .orElse(Integer.MAX_VALUE);
    
    // 最小人数のチームを全て取得
    List<Team> smallestTeams = candidateTeams.stream()
        .filter(t -> t.getMembers().size() == minSize)
        .toList();
    
    // 同じ人数の場合はランダムに選択
    Random random = new Random();
    return smallestTeams.get(random.nextInt(smallestTeams.size()));
  }
}
