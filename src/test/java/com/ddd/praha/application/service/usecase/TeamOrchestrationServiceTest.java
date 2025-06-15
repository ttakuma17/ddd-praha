package com.ddd.praha.application.service.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.ddd.praha.application.repository.TeamRepository;
import com.ddd.praha.application.service.domain.TeamCompositionDomainService;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.Team;
import com.ddd.praha.domain.model.Email;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.model.MemberName;
import com.ddd.praha.domain.model.TeamComposition;
import com.ddd.praha.domain.model.TeamCompositionResult;
import com.ddd.praha.domain.model.TeamId;
import com.ddd.praha.domain.model.TeamName;
import com.ddd.praha.domain.model.TeamRedistributionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TeamOrchestrationServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TeamCompositionDomainService domainService;

    private TeamOrchestrationService service;

    private Team testTeam;
    private Member testMember1;
    private Member testMember2;
    private Member testMember3;
    private Member testMember4;

    @BeforeEach
    void setUp() {
        service = new TeamOrchestrationService(teamRepository, notificationService, domainService);

        // テストデータの準備
        testMember1 = new Member(
            new MemberId("member-001"),
            new MemberName("田中太郎"),
            new Email("tanaka@example.com"),
            EnrollmentStatus.在籍中
        );

        testMember2 = new Member(
            new MemberId("member-002"),
            new MemberName("佐藤花子"),
            new Email("sato@example.com"),
            EnrollmentStatus.在籍中
        );

        testMember3 = new Member(
            new MemberId("member-003"),
            new MemberName("鈴木一郎"),
            new Email("suzuki@example.com"),
            EnrollmentStatus.在籍中
        );

        testMember4 = new Member(
            new MemberId("member-004"),
            new MemberName("佐野二郎"),
            new Email("sano@example.com"),
            EnrollmentStatus.在籍中
        );

        testTeam = new Team(
            new TeamId("team-001"),
            new TeamName("TestTeam"),
            Arrays.asList(testMember1, testMember2)
        );
    }

    @Test
    void 合流先が見つからない場合にメール通知が送信される() {
        // 準備
        TeamId teamId = testTeam.getId();
        List<Team> allTeams = List.of(testTeam);
        
        when(teamRepository.get(teamId)).thenReturn(testTeam);
        when(teamRepository.getAll()).thenReturn(allTeams);
        
        TeamComposition noChangeComposition = TeamComposition.noChange(testTeam);
        TeamRedistributionResult mergeFailureResult = TeamRedistributionResult.mergeFailure(
            noChangeComposition, testMember1
        );
        when(domainService.executeRedistribution(testTeam, testMember1, allTeams))
            .thenReturn(mergeFailureResult);

        // 実行
        service.removeMemberFromTeam(teamId, testMember1);

        // 検証
        verify(notificationService).notifyMergeFailure(
            eq(testTeam), 
            eq(testMember1)
        );
    }

    @Test
    void 復帰したメンバーを適切なチームに割り当てる() {
        // 準備
        Team smallestTeam = new Team(
            new TeamId("team-002"),
            new TeamName("SmallestTeam"),
            Arrays.asList(testMember3, testMember4)
        );
        
        List<Team> allTeams = Arrays.asList(testTeam, smallestTeam);
        when(teamRepository.getAll()).thenReturn(allTeams);
        
        TeamComposition composition = TeamComposition.noChange(smallestTeam);
        TeamCompositionResult result = TeamCompositionResult.normal(composition);
        // 新しいメンバーを作成
        Member newMember = new Member(
            new MemberId("member-005"),
            new MemberName("新規メンバー"),
            new Email("new@example.com"),
            EnrollmentStatus.在籍中
        );
        
        when(domainService.assignMemberToTeam(newMember, allTeams))
            .thenReturn(result);

        // 実行
        service.assignMemberToTeam(newMember);

        // 検証
        verify(teamRepository).addMember(smallestTeam.getId(), newMember.getId());
    }

    @Test
    void 復帰時にチーム分割が必要な場合は新しいチームを作成する() {
        // 準備
        Team originalTeam = new Team(
            new TeamId("team-001"),
            new TeamName("OriginalTeam"),
            Arrays.asList(testMember1, testMember2)
        );
        
        Team newTeam = new Team(
            new TeamName("SplitTeam"),
            List.of(testMember3, testMember4)
        );
        
        List<Team> allTeams = List.of(originalTeam);
        when(teamRepository.getAll()).thenReturn(allTeams);
        
        TeamComposition splitComposition = TeamComposition.split(originalTeam, newTeam, List.of(testMember3));
        TeamCompositionResult splitResult = TeamCompositionResult.split(splitComposition);
        when(domainService.assignMemberToTeam(testMember3, allTeams))
            .thenReturn(splitResult);

        // 実行
        service.assignMemberToTeam(testMember3);

        // 検証
        verify(teamRepository).create(newTeam);
        verify(notificationService).notifyTeamSplit(originalTeam, newTeam);
    }

    @Test
    void チーム分割が必要な場合は新しいチームを作成して通知する() {
        // 準備
        TeamId teamId = testTeam.getId();
        
        Team newTeam = new Team(
            new TeamName("SplitTeam"),
            List.of(testMember3,testMember4)
        );
        
        when(teamRepository.get(teamId)).thenReturn(testTeam);
        
        TeamComposition splitComposition = TeamComposition.split(testTeam, newTeam, List.of(testMember3));
        TeamCompositionResult splitResult = TeamCompositionResult.split(splitComposition);
        when(domainService.executeComposition(testTeam, testMember3))
            .thenReturn(splitResult);

        // 実行
        Team result = service.addMemberToTeam(teamId, testMember3);

        // 検証
        verify(teamRepository).create(newTeam);
        verify(notificationService).notifyTeamSplit(testTeam, newTeam);
        assertEquals(testTeam, result);
    }
}