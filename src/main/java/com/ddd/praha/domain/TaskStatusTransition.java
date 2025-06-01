package com.ddd.praha.domain;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TaskStatusTransition {
  Map<TaskStatus, Set<TaskStatus>> allowed;
  {
    allowed = new HashMap<>();
    allowed.put(TaskStatus.未着手, EnumSet.of(TaskStatus.取組中));
    allowed.put(TaskStatus.取組中, EnumSet.of(TaskStatus.レビュー待ち));
    allowed.put(TaskStatus.レビュー待ち, EnumSet.of(TaskStatus.取組中, TaskStatus.完了));
  }

  boolean canTransit(TaskStatus from, TaskStatus to) {
    Set<TaskStatus> allowedStates = allowed.get(from);
    return allowedStates.contains(to);
  }
}
