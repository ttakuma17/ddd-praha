package com.ddd.praha.application.service;

import com.ddd.praha.application.repository.NotificationRepository;
import com.ddd.praha.domain.*;
import com.ddd.praha.application.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
        List<Team> allTeams = teamRepository.getAll();

        TeamCompositionResult result = domainService.executeComposition(team, member, allTeams);

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

        return result.composition().getOriginalTeam();
    }
}
