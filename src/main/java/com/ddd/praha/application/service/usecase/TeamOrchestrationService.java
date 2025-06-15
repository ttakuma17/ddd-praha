package com.ddd.praha.application.service.usecase;

import com.ddd.praha.application.repository.TeamRepository;
import com.ddd.praha.application.service.domain.TeamCompositionDomainService;
import com.ddd.praha.domain.entity.*;
import com.ddd.praha.domain.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * チーム編成オーケストレーションサービス。
 * 
 * <p>チームメンバーの追加・削除に伴う複雑なビジネスルールの実行を統合的に管理する。
 * チーム分割、合流、監視通知など、チーム編成の自動化処理を担当する。</p>
 * 
 * <p>主な責務：</p>
 * <ul>
 *   <li>チームメンバーの追加・削除の統合処理</li>
 *   <li>チーム分割条件の判定と実行（5名以上時）</li>
 *   <li>チーム合流処理の実行（1名時）</li>
 *   <li>チーム監視通知の送信（2名以下時）</li>
 *   <li>復帰メンバーの最適チーム割り当て</li>
 * </ul>
 * 
 * <p>全てのメソッドはトランザクション境界内で実行され、
 * データ整合性を保証している。</p>
 */
@Service
@Transactional
public class TeamOrchestrationService {
    private static final Logger logger = LoggerFactory.getLogger(TeamOrchestrationService.class);
    private final TeamRepository teamRepository;
    private final NotificationService notificationService;
    private final TeamCompositionDomainService domainService;

    /**
     * TeamOrchestrationServiceのコンストラクタ。
     * 
     * @param teamRepository チームリポジトリ
     * @param notificationService 通知サービス
     * @param domainService チーム編成ドメインサービス
     */
    public TeamOrchestrationService(TeamRepository teamRepository,
        NotificationService notificationService, TeamCompositionDomainService domainService) {
        this.teamRepository = teamRepository;
        this.notificationService = notificationService;
        this.domainService = domainService;
    }

    /**
     * 指定されたチームにメンバーを追加する。
     * 
     * <p>メンバー追加後、チーム人数が5名以上になった場合は自動的に2つのチームに分割される。
     * 分割時は新しいチームがリポジトリに保存され、管理者に通知が送信される。</p>
     * 
     * <p>処理フロー：</p>
     * <ol>
     *   <li>指定されたチームを取得</li>
     *   <li>ドメインサービスによるチーム編成実行</li>
     *   <li>分割が必要な場合は新チーム作成と通知送信</li>
     *   <li>更新されたチーム情報を返却</li>
     * </ol>
     * 
     * @param teamId 追加対象のチームID
     * @param member 追加するメンバー
     * @return 更新後のチーム（分割時は元のチーム）
     * @throws RuntimeException チームが見つからない場合
     * @throws IllegalArgumentException メンバーが参加条件を満たさない場合
     */
    public Team addMemberToTeam(TeamId teamId, Member member) {
        Team team = teamRepository.get(teamId);

        TeamCompositionResult result = domainService.executeComposition(team, member);

        // チーム分割が必要な場合
        if (result.requiresSplit()) {
            Team newTeam = result.composition().getNewTeam();
            Team originalTeam = result.composition().getOriginalTeam();

            teamRepository.create(newTeam);
            notificationService.notifyTeamSplit(originalTeam, newTeam);
            logger.info("チーム分割: {} -> {}", originalTeam.getName(), newTeam.getName());

            return originalTeam;
        }

        return result.composition().getOriginalTeam();
    }

    public Team removeMemberFromTeam(TeamId teamId, Member member) {
        Team team = teamRepository.get(teamId);
        List<Team> allTeams = teamRepository.getAll();

        TeamRedistributionResult result = domainService.executeRedistribution(team, member, allTeams);

        // 監視が必要な場合
        if (result.requiresMonitoring()) {
            notificationService.notifyTeamMonitoring(
                result.composition().getOriginalTeam(),
                result.removedMember()
            );
        }

        // 合流が必要な場合
        if (result.requiresMerge()) {
            Team mergedTeam = result.composition().getOriginalTeam();
            teamRepository.delete(team);
            notificationService.notifyTeamMerge(mergedTeam, result.removedMember());
            logger.info("チーム合流: {} -> {}", team.getName(), mergedTeam.getName());
            return mergedTeam;
        }

        // 合流失敗の場合
        if (result.mergeFailure()) {
            notificationService.notifyMergeFailure(
                result.composition().getOriginalTeam(),
                result.composition().getOriginalTeam().getMembers().get(0)
            );
        }

        return result.composition().getOriginalTeam();
    }

    /**
     * 復帰したメンバーを適切なチームに割り当てる
     *
     * @param member 復帰するメンバー
     */
    public void assignMemberToTeam(Member member) {
        List<Team> allTeams = teamRepository.getAll();
        
        TeamCompositionResult result = domainService.assignMemberToTeam(member, allTeams);
        
        // チーム分割が必要な場合
        if (result.requiresSplit()) {
            Team newTeam = result.composition().getNewTeam();
            Team originalTeam = result.composition().getOriginalTeam();
            
            teamRepository.create(newTeam);
            notificationService.notifyTeamSplit(originalTeam, newTeam);
            logger.info("復帰時にチーム分割: {} -> {}", originalTeam.getName(), newTeam.getName());
            
            return;
        }
        
        // 通常の追加
        teamRepository.addMember(result.composition().getOriginalTeam().getId(), member.getId());
        logger.info("メンバー {} をチーム {} に割り当てました", 
            member.getName().value(), 
            result.composition().getOriginalTeam().getName().value());

    }
}
