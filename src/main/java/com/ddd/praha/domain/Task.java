package com.ddd.praha.domain;

public class Task {
  TaskId id;
  TaskName name;

  public Task(TaskName name) {
    this.id = TaskId.generate();
    this.name = name;
  }
}
