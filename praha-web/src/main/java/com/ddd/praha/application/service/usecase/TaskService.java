package com.ddd.praha.application.service.usecase;

import com.ddd.praha.application.repository.TaskProgressRepository;
import com.ddd.praha.application.repository.TaskRepository;
import com.ddd.praha.domain.entity.*;
import com.ddd.praha.domain.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 課題管理のアプリケーションサービス。
 * 
 * <p>プラハチャレンジの学習課題に関する操作を統合管理する。
 * 課題の作成・取得から、参加者の進捗ステータス更新まで、
 * 課題ドメインのユースケースを実装する。</p>
 * 
 * <p>主な機能：</p>
 * <ul>
 *   <li>課題の作成と取得</li>
 *   <li>課題進捗ステータスの更新（権限チェック付き）</li>
 *   <li>課題と進捗情報の統合管理</li>
 * </ul>
 * 
 * <p>このサービスは課題エンティティと課題進捗エンティティを協調させ、
 * ドメインルールに従った安全な操作を提供する。</p>
 */
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskProgressRepository taskProgressRepository;

    /**
     * TaskServiceのコンストラクタ。
     * 
     * @param taskRepository 課題リポジトリ
     * @param taskProgressRepository 課題進捗リポジトリ
     */
    public TaskService(TaskRepository taskRepository, TaskProgressRepository taskProgressRepository) {
        this.taskRepository = taskRepository;
        this.taskProgressRepository = taskProgressRepository;
    }

    /**
     * 全ての課題を取得する。
     * 
     * @return 全課題のリスト
     */
    public List<Task> findAll() {
        return taskRepository.findAll();
    }
    
    /**
     * 指定されたIDの課題を取得する。
     * 
     * @param id 課題ID
     * @return 課題エンティティ
     * @throws RuntimeException 課題が見つからない場合
     */
    public Task get(TaskId id) {
        return taskRepository.get(id);
    }

    /**
     * 新しい課題を作成して保存する。
     * 
     * <p>課題IDは自動生成され、リポジトリに保存される。</p>
     * 
     * @param name 課題名
     */
    public void addTask(TaskName name) {
        Task newTask = new Task(name);
        taskRepository.save(newTask);
    }

    /**
     * 参加者の課題進捗ステータスを更新する。
     * 
     * <p>課題進捗の更新には権限チェックが適用され、
     * 課題の所有者のみが自分の進捗を更新できる。
     * ステータス遷移ルールも自動的に検証される。</p>
     * 
     * <p>処理フロー：</p>
     * <ol>
     *   <li>指定されたメンバーと課題の進捗情報を取得</li>
     *   <li>操作者の権限をチェック（所有者のみ更新可能）</li>
     *   <li>ステータス遷移ルールを検証</li>
     *   <li>進捗ステータスを更新してリポジトリに保存</li>
     * </ol>
     *
     * @param operator  操作を行う参加者（権限チェック用）
     * @param member    課題の所有者である参加者
     * @param task      更新する課題
     * @param newStatus 新しい進捗ステータス
     * @throws IllegalArgumentException 参加者課題が存在しない場合
     * @throws IllegalArgumentException 操作者に更新権限がない場合
     * @throws IllegalStateException ステータス遷移が許可されていない場合
     */
    public void updateTaskStatus(Member operator, Member member, Task task, TaskStatus newStatus) {
        TaskProgress taskProgress = taskProgressRepository.findByMemberAndTask(member, task);
        if (taskProgress == null) {
            throw new IllegalArgumentException("指定された課題が見つかりません");
        }
        
        taskProgress.updateTaskStatus(operator, task, newStatus);
        taskProgressRepository.save(taskProgress, task);
    }

}