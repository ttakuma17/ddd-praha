package com.ddd.praha.application.repository;

import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberTask;
import com.ddd.praha.domain.Task;

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
    Optional<MemberTask> findByMember(Member member);
    
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
}