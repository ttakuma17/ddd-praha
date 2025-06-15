package com.ddd.praha.domain.entity;

import com.ddd.praha.domain.model.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 参加者課題エンティティ
 */
public class TaskProgress {
  private final Member owner;
  private final Map<Task, TaskStatus> map;

  public Member getOwner() {
    return owner;
  }

  public TaskProgress(Member member, List<Task> tasks) {
    this.owner = Objects.requireNonNull(member, "所有者は必須です");
    Objects.requireNonNull(tasks, "課題リストは必須です");
    this.map = new HashMap<>();
    tasks.forEach(task -> map.put(task, TaskStatus.未着手));
  }

  public TaskProgress(Member owner, Map<Task, TaskStatus> taskStatuses) {
    this.owner = Objects.requireNonNull(owner, "所有者は必須です");
    this.map = Objects.requireNonNull(taskStatuses, "課題ステータスマップは必須です");
  }

  public void updateTaskStatus(Member operator, Task task, TaskStatus newStatus) {
    if (!owner.equals(operator)) {
      throw new IllegalArgumentException("進捗ステータスを変更できるのは、課題の所有者だけです");
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
