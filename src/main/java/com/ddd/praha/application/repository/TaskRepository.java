package com.ddd.praha.application.repository;

import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskId;

import java.util.List;
import java.util.Optional;

/**
 * 課題リポジトリインターフェース
 */
public interface TaskRepository {
    /**
     * 全ての課題を取得する
     * @return 課題のリスト
     */
    List<Task> findAll();
    
    /**
     * IDで課題を検索する
     * @param id 課題ID
     * @return 課題（存在しない場合はEmpty）
     */
    Task findById(TaskId id);
    
    /**
     * 課題を保存する（新規追加または更新）
     * @param task 保存する課題
     * @return 保存された課題
     */
    Task save(Task task);
}