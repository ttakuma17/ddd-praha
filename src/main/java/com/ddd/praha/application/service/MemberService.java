package com.ddd.praha.application.service;

import com.ddd.praha.domain.Email;
import com.ddd.praha.domain.EnrollmentStatus;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberName;
import com.ddd.praha.application.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 参加者サービス
 */
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    
    /**
     * 全ての参加者を取得する
     * @return 参加者のリスト
     */
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
    
    /**
     * IDで参加者を検索する
     * @param id 参加者ID
     * @return 参加者（存在しない場合はEmpty）
     */
    public Optional<Member> getMemberById(MemberId id) {
        return memberRepository.findById(id);
    }
    
    /**
     * 新しい参加者を追加する
     * @param name 参加者名
     * @param email メールアドレス
     * @param status 在籍ステータス
     * @return 追加された参加者
     */
    public Member addMember(MemberName name, Email email, EnrollmentStatus status) {
        Member newMember = new Member(name, email, status);
        return memberRepository.save(newMember);
    }
    
    /**
     * 参加者の在籍ステータスを更新する
     * @param id 参加者ID
     * @param newStatus 新しい在籍ステータス
     * @return 更新された参加者
     * @throws IllegalArgumentException 参加者が存在しない場合
     */
    public Member updateMemberStatus(MemberId id, EnrollmentStatus newStatus) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("指定されたIDの参加者が見つかりません"));
        
        member.updateEnrollmentStatus(newStatus);
        return memberRepository.save(member);
    }
}