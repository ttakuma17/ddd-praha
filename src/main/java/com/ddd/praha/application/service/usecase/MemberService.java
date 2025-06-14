package com.ddd.praha.application.service.usecase;

import com.ddd.praha.domain.Email;
import com.ddd.praha.domain.EnrollmentStatus;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberName;
import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.TeamId;
import com.ddd.praha.application.repository.MemberRepository;
import com.ddd.praha.application.repository.TeamRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 参加者サービス
 */
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TeamOrchestrationService teamOrchestrationService;
    
    public MemberService(MemberRepository memberRepository, 
                        TeamRepository teamRepository,
                        TeamOrchestrationService teamOrchestrationService) {
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
        this.teamOrchestrationService = teamOrchestrationService;
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
        EnrollmentStatus oldStatus = member.getStatus();
        
        // ステータスを更新
        member.updateEnrollmentStatus(newStatus);
        memberRepository.updateStatus(member.getId(), newStatus);
        
        // チーム再編成の処理
        handleTeamReorganization(member, oldStatus, newStatus);
    }
    
    /**
     * ステータス変更に応じたチーム再編成を処理する
     */
    private void handleTeamReorganization(Member member, EnrollmentStatus oldStatus, EnrollmentStatus newStatus) {
        // 在籍中に復帰した場合
        if (newStatus == EnrollmentStatus.在籍中 && oldStatus != EnrollmentStatus.在籍中) {
            teamOrchestrationService.assignMemberToTeam(member);
            return;
        }
        
        // 休会・退会した場合
        if (oldStatus == EnrollmentStatus.在籍中 && newStatus != EnrollmentStatus.在籍中) {
            // 現在所属しているチームを探す
            Optional<Team> currentTeam = findMemberTeam(member.getId());
          currentTeam.ifPresent(
              team -> teamOrchestrationService.removeMemberFromTeam(team.getId(), member));
        }
    }
    
    /**
     * メンバーが所属しているチームを探す
     */
    private Optional<Team> findMemberTeam(MemberId memberId) {
        List<Team> allTeams = teamRepository.getAll();
        return allTeams.stream()
            .filter(team -> team.getMembers().stream()
                .anyMatch(member -> member.getId().equals(memberId)))
            .findFirst();
    }
}