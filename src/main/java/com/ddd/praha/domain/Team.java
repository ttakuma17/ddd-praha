package com.ddd.praha.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * チーム集約？
 */
public class Team {
  TeamId id;
  TeamName name;
  List<Member> list;

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
    if (member.canJoin()){
      throw new IllegalArgumentException("在籍中ではない参加者はチームに追加できません");
    };
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
}
