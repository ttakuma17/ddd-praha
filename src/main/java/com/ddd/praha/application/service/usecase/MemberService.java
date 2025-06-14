package com.ddd.praha.application.service.usecase;

import com.ddd.praha.application.repository.MemberRepository;
import com.ddd.praha.application.repository.TeamRepository;
import com.ddd.praha.domain.entity.*;
import com.ddd.praha.domain.model.*;
import java.util.Optional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 参加者に関するアプリケーションサービス。
 * 
 * <p>参加者のCRUD操作および在籍ステータス変更に伴うチーム再編成ロジックを担当する。
 * 参加者の状態変更がチーム構成に与える影響を管理し、必要に応じてチーム編成サービスと連携する。</p>
 * 
 * <p>主な責務：</p>
 * <ul>
 *   <li>参加者の登録・更新・削除</li>
 *   <li>在籍ステータス変更とチーム再編成の統合処理</li>
 *   <li>参加者検索機能の提供</li>
 * </ul>
 * 
 */
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TeamOrchestrationService teamOrchestrationService;
    
    /**
     * MemberServiceのコンストラクタ。
     * 
     * @param memberRepository 参加者リポジトリ
     * @param teamRepository チームリポジトリ
     * @param teamOrchestrationService チーム編成オーケストレーションサービス
     */
    public MemberService(MemberRepository memberRepository, 
                        TeamRepository teamRepository,
                        TeamOrchestrationService teamOrchestrationService) {
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
        this.teamOrchestrationService = teamOrchestrationService;
    }

    /**
     * 指定されたIDの参加者を取得する。
     * 
     * @param memberId 参加者ID
     * @return 参加者エンティティ
     * @throws RuntimeException 参加者が見つからない場合
     */
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

    /**
     * 課題名のリストで検索し、特定のステータスになっている参加者を検索する
     * @param taskNames 課題名のリスト（完全一致検索）
     * @param statuses ステータスのリスト
     * @param page ページ番号（0から開始）
     * @param size ページサイズ
     * @return 検索結果
     */
    public MemberSearchResult searchMembersByTaskNamesAndStatuses(
            List<String> taskNames,
            List<TaskStatus> statuses,
            int page,
            int size) {
        List<Member> members = memberRepository.findMembersByTaskNamesAndStatuses(taskNames, statuses, page, size);
        return new MemberSearchResult(members, page, size, members.size());
    }
}