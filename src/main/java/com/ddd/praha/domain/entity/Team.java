package com.ddd.praha.domain.entity;

import com.ddd.praha.domain.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * プラハチャレンジにおけるチームを表すエンティティ。
 * 
 * <p>チームは2〜4名の在籍中メンバーで構成され、学習活動の基本単位となる。
 * チームの編成・分割・合流に関する複雑なビジネスルールを実装している。</p>
 * 
 * <p>チーム編成のルール：</p>
 * <ul>
 *   <li>メンバー数は2〜4名の範囲で維持される</li>
 *   <li>5名になった場合は自動的に2つのチームに分割される</li>
 *   <li>1名になった場合は他のチームとの合流が試行される</li>
 *   <li>2名以下になった場合は管理者への監視通知が送信される</li>
 * </ul>
 * 
 * <p>メンバーの参加資格：</p>
 * <ul>
 *   <li>在籍ステータスが「在籍中」のメンバーのみ参加可能</li>
 *   <li>同一チーム内での重複メンバーは不可</li>
 * </ul>
 * 
 */
public class Team {
  private final TeamId id;
  private final TeamName name;
  private final List<Member> list;

  /**
   * チームIDを取得する。
   * 
   * @return チームID
   */
  public TeamId getId() {
    return id;
  }

  /**
   * チーム名を取得する。
   * 
   * @return チーム名
   */
  public TeamName getName() {
    return name;
  }

  /**
   * チームに所属するメンバーのリストを取得する。
   * 
   * <p>返されるリストは防御的コピーで、変更しても元のチーム状態には影響しない。</p>
   * 
   * @return メンバーリストのコピー
   */
  public List<Member> getMembers() {
    return new ArrayList<>(list);
  }

  /**
   * 既存のIDを指定してチームを復元する（主にリポジトリからの復元用）。
   * 
   * @param id チームID（必須）
   * @param name チーム名（必須）
   * @param list 初期メンバーリスト（必須、2〜4名）
   * @throws NullPointerException いずれかの引数がnullの場合
   * @throws IllegalArgumentException メンバー数が適切でない場合
   */
  public Team(TeamId id, TeamName name, List<Member> list) {
    this.id = Objects.requireNonNull(id, "チームIDは必須です");
    this.name = Objects.requireNonNull(name, "チーム名は必須です");
    validateMembers(list);
    this.list = new ArrayList<>(list);
  }

  /**
   * 新しいチームを作成する（IDは自動生成）。
   * 
   * @param name チーム名（必須）
   * @param list 初期メンバーリスト（必須、2〜4名）
   * @throws NullPointerException いずれかの引数がnullの場合
   * @throws IllegalArgumentException メンバー数が適切でない場合
   */
  public Team(TeamName name, List<Member> list) {
    this.id = TeamId.generate();
    this.name = Objects.requireNonNull(name, "チーム名は必須です");
    validateMembers(list);
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

  /**
   * チームにメンバーを追加する。
   * 
   * <p>追加されるメンバーは以下の条件を満たす必要がある：</p>
   * <ul>
   *   <li>在籍ステータスが「在籍中」であること</li>
   *   <li>既にこのチームに所属していないこと</li>
   * </ul>
   * 
   * @param member 追加するメンバー（必須）
   * @throws IllegalArgumentException メンバーが参加条件を満たさない場合
   */
  public void addMember(Member member){
    if (!member.canJoin()){
      throw new IllegalArgumentException("在籍中ではない参加者はチームに追加できません");
    }
    if (list.contains(member)) {
      throw new IllegalArgumentException("指定された参加者は既にチームに所属しています");
    }
    list.add(member);
  }

  /**
   * チームからメンバーを削除する。
   * 
   * @param member 削除するメンバー（必須、このチームに所属している必要がある）
   * @throws IllegalArgumentException 指定されたメンバーがチームに所属していない場合
   */
  public void deleteMember(Member member){
    if (!list.contains(member)) {
      throw new IllegalArgumentException("指定された参加者はチームに所属していません");
    }
    list.remove(member);
  }

  /**
   * このチームが監視対象かどうかを判定する。
   * 
   * <p>チームメンバーが2名以下の場合、管理者による監視が必要となる。</p>
   * 
   * @return 監視が必要な場合はtrue、そうでなければfalse
   */
  public boolean needsMonitoring(){
    return list.size() <= 2;
  }

  /**
   * このチームが再編成対象かどうかを判定する。
   * 
   * <p>チームメンバーが1名のみの場合、他のチームとの合流が必要となる。</p>
   * 
   * @return 再編成が必要な場合はtrue、そうでなければfalse
   */
  public boolean needsRedistribution(){
    return list.size() == 1;
  }

  /**
   * このチームが分割対象かどうかを判定する。
   * 
   * <p>チームメンバーが5名以上の場合、2つのチームに分割される。</p>
   * 
   * @return 分割が必要な場合はtrue、そうでなければfalse
   */
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
    TeamName newTeamName = new TeamName(this.name.value() + "Split");
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
