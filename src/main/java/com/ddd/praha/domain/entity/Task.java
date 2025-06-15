package com.ddd.praha.domain.entity;

import com.ddd.praha.domain.model.*;
import java.util.Objects;

public class Task {
  private final TaskId id;
  private final TaskName name;

  public TaskId getId() {
    return id;
  }

  public TaskName getName() {
    return name;
  }

  public Task(TaskName name) {
    this.id = TaskId.generate();
    this.name = Objects.requireNonNull(name, "課題名は必須です");
  }

  public Task(TaskId id, TaskName name) {
    this.id = Objects.requireNonNull(id, "課題IDは必須です");
    this.name = Objects.requireNonNull(name, "課題名は必須です");
  }
}
