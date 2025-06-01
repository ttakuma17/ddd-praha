package com.ddd.praha.application.repository;

import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;

import java.util.List;
import java.util.Optional;

/**
 * 参加者リポジトリインターフェース
 */
public interface MemberRepository {
    /**
     * 全ての参加者を取得する
     * @return 参加者のリスト
     */
    List<Member> findAll();
    
    /**
     * IDで参加者を検索する
     * @param id 参加者ID
     * @return 参加者（存在しない場合はEmpty）
     */
    Optional<Member> findById(MemberId id);
    
    /**
     * 参加者を保存する（新規追加または更新）
     * @param member 保存する参加者
     * @return 保存された参加者
     */
    Member save(Member member);
}