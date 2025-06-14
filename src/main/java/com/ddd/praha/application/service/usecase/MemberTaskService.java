package com.ddd.praha.application.service.usecase;

import com.ddd.praha.application.repository.MemberTaskRepository;
import com.ddd.praha.domain.entity.*;
import com.ddd.praha.domain.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public MemberTask getMemberTask(Member member) {
        return memberTaskRepository.getByMember(member);
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
        MemberTask memberTask = memberTaskRepository.getByMember(member);
        memberTask.updateTaskStatus(operator, task, newStatus);
        return memberTaskRepository.save(memberTask);
    }

    /**
     * 指定された課題群が指定されたステータスになっている参加者をページングして取得する
     * @param taskIds 課題IDのリスト
     * @param statuses ステータスのリスト
     * @param page ページ番号（0から開始）
     * @param size ページサイズ
     * @return メンバー検索結果
     */
    public MemberSearchResult findMembersByTasksAndStatuses(List<TaskId> taskIds, List<TaskStatus> statuses, int page, int size) {
        List<Member> members = memberTaskRepository.findMembersByTasksAndStatuses(taskIds, statuses, page, size);
        long totalElements = memberTaskRepository.countMembersByTasksAndStatuses(taskIds, statuses);
        
        return new MemberSearchResult(members, page, size, totalElements);
    }
}