package com.ddd.praha.application.service.usecase;

import com.ddd.praha.application.repository.MemberTaskRepository;
import com.ddd.praha.application.repository.TaskRepository;
import com.ddd.praha.domain.entity.*;
import com.ddd.praha.domain.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 課題サービス
 */
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final MemberTaskRepository memberTaskRepository;

    public TaskService(TaskRepository taskRepository, MemberTaskRepository memberTaskRepository) {
        this.taskRepository = taskRepository;
        this.memberTaskRepository = memberTaskRepository;
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

    /**
     * 参加者の課題進捗ステータスを更新する
     *
     * @param operator  操作を行う参加者
     * @param member    課題の所有者である参加者
     * @param task      更新する課題
     * @param newStatus 新しい進捗ステータス
     * @throws IllegalArgumentException 参加者課題が存在しない場合
     */
    public void updateTaskStatus(Member operator, Member member, Task task, TaskStatus newStatus) {
        MemberTask memberTask = memberTaskRepository.findByMemberAndTask(member, task);
        if (memberTask == null) {
            throw new IllegalArgumentException("指定された課題が見つかりません");
        }
        
        memberTask.updateTaskStatus(operator, task, newStatus);
        memberTaskRepository.save(memberTask, task);
    }

}