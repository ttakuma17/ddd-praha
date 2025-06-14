package com.ddd.praha.domain.entity;

import com.ddd.praha.domain.model.*;

public class Task {
  TaskId id;
  TaskName name;

  public TaskId getId() {
    return id;
  }

  public TaskName getName() {
    return name;
  }

  public Task(TaskName name) {
    this.id = TaskId.generate();
    this.name = name;
  }

  public Task(TaskId id, TaskName name) {
    this.id = id;
    this.name = name;
  }
}
