package com.ddd.praha.application.service.usecase;

import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskId;
import com.ddd.praha.domain.TaskName;
import com.ddd.praha.application.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 課題サービス
 */
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    public List<Task> findAll() {
        return taskRepository.findAll();
    }
    
    public Task get(TaskId id) {
        return taskRepository.get(id);
    }

    public void addTask(TaskName name) {
        Task newTask = new Task(name);
        taskRepository.save(newTask);
    }
}