package com.ddd.praha.infrastructure;


import com.ddd.praha.application.repository.TaskRepository;
import com.ddd.praha.domain.entity.Task;
import com.ddd.praha.domain.model.TaskId;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 課題リポジトリのMyBatis実装
 */
@Repository
public class TaskRepositoryImpl implements TaskRepository {

  private final TaskMapper taskMapper;

  public TaskRepositoryImpl(TaskMapper taskMapper) {
    this.taskMapper = taskMapper;
  }

  @Override
  public List<Task> findAll() {
    List<TaskRecord> all = taskMapper.findAll();
    return all.stream().map(TaskRecord::toTask).toList();
  }

  @Override
  public Task get(TaskId id) {
    TaskRecord taskRecord = taskMapper.get(id);
    if (taskRecord == null) {
      throw new IllegalStateException("Task record is null.");
    }
    return taskRecord.toTask();
  }

  @Override
  public void save(Task task) {
    taskMapper.insert(task);
  }
}