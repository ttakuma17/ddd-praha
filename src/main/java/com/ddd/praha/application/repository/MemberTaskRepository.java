package com.ddd.praha.application.repository;

import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.MemberTask;
import com.ddd.praha.domain.entity.Task;

/**
 * 参加者課題リポジトリインターフェース
 */
public interface MemberTaskRepository {
    
    /**
     * 参加者と課題で参加者課題を検索する
     * @param member 参加者
     * @param task 課題
     * @return 参加者課題（存在しない場合はnull）
     */
    MemberTask findByMemberAndTask(Member member, Task task);
    
    /**
     * 参加者課題を保存する
     * @param memberTask 参加者課題
     * @param task 更新対象の課題
     */
    void save(MemberTask memberTask, Task task);
}