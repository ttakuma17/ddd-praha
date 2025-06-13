package com.ddd.praha.application.service;

import com.ddd.praha.domain.Email;
import com.ddd.praha.domain.EnrollmentStatus;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberName;
import com.ddd.praha.application.repository.MemberRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 参加者サービス
 */
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member get(MemberId memberId) {
        return memberRepository.get(memberId);
    }
    
    /**
     * 全ての参加者を取得する
     * @return 参加者のリスト
     */
    public List<Member> getAll() {
        return memberRepository.getAll();
    }
    
    /**
     * IDで参加者を検索する
     * @param id 参加者ID
     * @return 参加者（存在しない場合はEmpty）
     */
    public Optional<Member> findById(MemberId id) {
        return memberRepository.findById(id);
    }
    
    /**
     * 新しい参加者を追加する
     * @param name 参加者名
     * @param email メールアドレス
     * @param status 在籍ステータス
     * @return 追加された参加者
     */
    public void addMember(MemberName name, Email email, EnrollmentStatus status) {
        Member newMember = new Member(name, email, status);
        memberRepository.save(newMember);
    }
    
    /**
     * 参加者の在籍ステータスを更新する
     * @param id 参加者ID
     * @param newStatus 新しい在籍ステータス
     * @throws IllegalArgumentException 参加者が存在しない場合
     */
    public void updateMemberStatus(MemberId id, EnrollmentStatus newStatus) {
        Member member = memberRepository.get(id);
        member.updateEnrollmentStatus(newStatus);
        memberRepository.updateStatus(member);
    }
}