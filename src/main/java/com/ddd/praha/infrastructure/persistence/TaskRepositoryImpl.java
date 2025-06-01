package com.ddd.praha.infrastructure.persistence;

import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskId;
import com.ddd.praha.application.repository.TaskRepository;
import com.ddd.praha.infrastructure.persistence.mapper.TaskMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
        return taskMapper.findAll();
    }

    @Override
    public Optional<Task> findById(TaskId id) {
        Task task = taskMapper.findById(id.value());
        return Optional.ofNullable(task);
    }

    @Override
    public Task save(Task task) {
        if (taskMapper.exists(task.getId().value())) {
            taskMapper.update(
                task.getId().value(),
                task.getName().value()
            );
        } else {
            taskMapper.insert(
                task.getId().value(),
                task.getName().value()
            );
        }
        return task;
    }
}