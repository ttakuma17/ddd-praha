package com.ddd.praha.application.service;

import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberTask;
import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskStatus;
import com.ddd.praha.application.repository.MemberTaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 参加者課題サービス
 */
@Service
public class MemberTaskService {
    private final MemberTaskRepository memberTaskRepository;
    
    public MemberTaskService(MemberTaskRepository memberTaskRepository) {
        this.memberTaskRepository = memberTaskRepository;
    }
    
    /**
     * 特定の参加者の課題を取得する
     * @param member 参加者
     * @return 参加者の課題
     */
    public Optional<MemberTask> getMemberTask(Member member) {
        return memberTaskRepository.findByMember(member);
    }
    
    /**
     * 特定の課題に取り組んでいる全ての参加者の課題を取得する
     * @param task 課題
     * @return 参加者課題のリスト
     */
    public List<MemberTask> getMemberTasksByTask(Task task) {
        return memberTaskRepository.findByTask(task);
    }
    
    /**
     * 参加者の課題進捗ステータスを更新する
     * @param operator 操作を行う参加者
     * @param member 課題の所有者である参加者
     * @param task 更新する課題
     * @param newStatus 新しい進捗ステータス
     * @return 更新された参加者課題
     * @throws IllegalArgumentException 参加者課題が存在しない場合
     */
    public MemberTask updateTaskStatus(Member operator, Member member, Task task, TaskStatus newStatus) {
        MemberTask memberTask = memberTaskRepository.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("指定された参加者の課題が見つかりません"));
        
        memberTask.updateTaskStatus(operator, task, newStatus);
        return memberTaskRepository.save(memberTask);
    }
    
    /**
     * 参加者に新しい課題を割り当てる
     * @param member 参加者
     * @param tasks 課題のリスト
     * @return 作成された参加者課題
     */
    public MemberTask assignTasksToMember(Member member, List<Task> tasks) {
        MemberTask memberTask = new MemberTask(member, tasks);
        return memberTaskRepository.save(memberTask);
    }
}