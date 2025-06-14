package com.ddd.praha.application.repository;

import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberTask;
import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskId;
import com.ddd.praha.domain.TaskStatus;

import java.util.List;
import java.util.Optional;

/**
 * 参加者課題リポジトリインターフェース
 */
public interface MemberTaskRepository {
    /**
     * 特定の参加者の課題を取得する
     * @param member 参加者
     * @return 参加者の課題
     */
    MemberTask getByMember(Member member);
    
    /**
     * 特定の課題に取り組んでいる全ての参加者の課題を取得する
     * @param task 課題
     * @return 参加者課題のリスト
     */
    List<MemberTask> findByTask(Task task);
    
    /**
     * 参加者課題を保存する（新規追加または更新）
     * @param memberTask 保存する参加者課題
     * @return 保存された参加者課題
     */
    MemberTask save(MemberTask memberTask);
    
    /**
     * 指定された課題群が指定されたステータスになっている参加者をページングして取得する
     * @param taskIds 課題IDのリスト
     * @param statuses ステータスのリスト
     * @param page ページ番号（0から開始）
     * @param size ページサイズ
     * @return 条件に合致する参加者のリスト
     */
    List<Member> findMembersByTasksAndStatuses(List<TaskId> taskIds, List<TaskStatus> statuses, int page, int size);
    
    /**
     * 指定された課題群が指定されたステータスになっている参加者の総数を取得する
     * @param taskIds 課題IDのリスト
     * @param statuses ステータスのリスト
     * @return 条件に合致する参加者の総数
     */
    long countMembersByTasksAndStatuses(List<TaskId> taskIds, List<TaskStatus> statuses);
}