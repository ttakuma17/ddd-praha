package com.ddd.praha.application.service;

import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskId;
import com.ddd.praha.domain.TaskName;
import com.ddd.praha.application.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 課題サービス
 */
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    /**
     * 全ての課題を取得する
     * @return 課題のリスト
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    /**
     * IDで課題を検索する
     * @param id 課題ID
     * @return 課題（存在しない場合はEmpty）
     */
    public Optional<Task> getTaskById(TaskId id) {
        return taskRepository.findById(id);
    }
    
    /**
     * 新しい課題を作成する
     * @param name 課題名
     * @return 作成された課題
     */
    public Task createTask(TaskName name) {
        Task newTask = new Task(name);
        return taskRepository.save(newTask);
    }
    
    /**
     * 新しい課題を追加する（管理者用API向け）
     * @param name 課題名
     * @return 作成された課題
     */
    public Task addTask(TaskName name) {
        Task newTask = new Task(name);
        return taskRepository.save(newTask);
    }
}