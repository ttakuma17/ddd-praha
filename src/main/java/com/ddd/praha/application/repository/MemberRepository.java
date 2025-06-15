package com.ddd.praha.application.repository;

import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.model.TaskId;
import com.ddd.praha.domain.model.TaskStatus;
import java.util.List;
import java.util.Optional;

/**
 * 参加者リポジトリインターフェース
 */
public interface MemberRepository {
    Member get(MemberId id);

    /**
     * 全ての参加者を取得する
     * @return 参加者のリスト
     */
    List<Member> getAll();
    
    /**
     * IDで参加者を検索する
     * @param id 参加者ID
     * @return 参加者（存在しない場合はEmpty）
     */
    Optional<Member> findById(MemberId id);
    
    /**
     * 参加者を保存する（新規追加または更新）
     * @param member 保存する参加者
     */
    void save(Member member);

    void updateStatus(MemberId id, EnrollmentStatus status);

    /**
     * 課題名のリストで検索し、指定されたステータスになっている参加者を検索する
     * @param taskNames 課題名のリスト（完全一致検索）
     * @param statuses ステータスのリスト
     * @param page ページ番号（0から開始）
     * @param size ページサイズ
     * @return 条件に合致する参加者のリスト
     */
    List<Member> findMembersByTaskNamesAndStatuses(List<String> taskNames, List<TaskStatus> statuses, int page, int size);

}