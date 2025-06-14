package com.ddd.praha.domain.entity;

import com.ddd.praha.domain.model.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参加者課題エンティティ
 */
public class MemberTask {
  Member owner;
  Map<Task, TaskStatus> map = new HashMap<>();

  public Member getOwner() {
    return owner;
  }

  public MemberTask(Member Member, List<Task> list) {
    this.owner = Member;
    list.forEach(Task -> map.put(Task, TaskStatus.未着手));
  }

  public MemberTask(Member owner, Map<Task, TaskStatus> map) {
    this.owner = owner;
    this.map = map;
  }

  public void updateTaskStatus(Member operator, Task task, TaskStatus newStatus) {
    if (!owner.equals(operator)) {
      throw new RuntimeException("進捗ステータスを変更できるのは、課題の所有者だけです");
    }
    var transition = new TaskStatusTransition();
    if (!transition.canTransit(map.get(task), newStatus)) {
      throw new IllegalStateException("このステータス変更は許可されていません");
    }
    map.put(task, newStatus);
  }

  public TaskStatus getTaskStatus(Task Task) {
    return map.get(Task);
  }

}
