package com.ddd.praha.domain.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TaskStatusTransition {
  private static final Map<TaskStatus, Set<TaskStatus>> ALLOWED_TRANSITIONS = 
      Map.of(
          TaskStatus.未着手, EnumSet.of(TaskStatus.取組中),
          TaskStatus.取組中, EnumSet.of(TaskStatus.レビュー待ち),
          TaskStatus.レビュー待ち, EnumSet.of(TaskStatus.取組中, TaskStatus.完了)
      );

  public boolean canTransit(TaskStatus from, TaskStatus to) {
    Objects.requireNonNull(from, "遷移元ステータスは必須です");
    Objects.requireNonNull(to, "遷移先ステータスは必須です");
    
    Set<TaskStatus> allowedStates = ALLOWED_TRANSITIONS.get(from);
    return allowedStates != null && allowedStates.contains(to);
  }
}
