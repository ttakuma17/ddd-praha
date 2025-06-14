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

@Service
@Transactional
public class TeamOrchestrationService {
    private static final Logger logger = LoggerFactory.getLogger(TeamOrchestrationService.class);
    private final TeamRepository teamRepository;
    private final NotificationService notificationService;
    private final TeamCompositionDomainService domainService;

    public TeamOrchestrationService(TeamRepository teamRepository,
        NotificationService notificationService, TeamCompositionDomainService domainService) {
        this.teamRepository = teamRepository;
        this.notificationService = notificationService;
        this.domainService = domainService;
    }


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
