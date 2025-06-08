package com.ddd.praha.application.service;

import com.ddd.praha.application.repository.NotificationRepository;
import com.ddd.praha.application.repository.TeamRepository;
import com.ddd.praha.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamOrchestrationServiceTest {

    @Mock
    private TeamRepository teamRepository;
    
    @Mock
    private NotificationRepository notificationRepository;

    private TeamOrchestrationService service;
    
    private Team testTeam;
    private TeamId testTeamId;
    private Member testMember;

    @BeforeEach
    void setUp() {
        service = new TeamOrchestrationService(teamRepository, notificationRepository);
        
        // テストデータの準備
        testTeamId = new TeamId("test-team-id");
        testMember = createTestMember("新メンバー", "new@example.com");
        
        List<Member> members = Arrays.asList(
            createTestMember("メンバー1", "member1@example.com"),
            createTestMember("メンバー2", "member2@example.com"),
            createTestMember("メンバー3", "member3@example.com")
        );
        
        testTeam = createTestTeam("テストチーム", members);
    }

    @Test
    void addMemberToTeam_NormalCase_AddsSuccessfully() {
        // Given
        when(teamRepository.findById(testTeamId)).thenReturn(Optional.of(testTeam));
        when(teamRepository.save(testTeam)).thenReturn(testTeam);

        // When
        Team result = service.addMemberToTeam(testTeamId, testMember);

        // Then
        assertNotNull(result);
        verify(teamRepository).findById(testTeamId);
        verify(teamRepository).save(testTeam);
        verifyNoInteractions(notificationRepository);
    }

    @Test
    void addMemberToTeam_TeamSplit_CreatesNewTeamAndSendsNotification() {
        // Given
        // 4名のチームを作成（5名目追加で分割が発生）
        List<Member> fourMembers = Arrays.asList(
            createTestMember("メンバー1", "member1@example.com"),
            createTestMember("メンバー2", "member2@example.com"),
            createTestMember("メンバー3", "member3@example.com"),
            createTestMember("メンバー4", "member4@example.com")
        );
        Team teamWithFourMembers = createTestTeam("フルチーム", fourMembers);
        
        when(teamRepository.findById(testTeamId)).thenReturn(Optional.of(teamWithFourMembers));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Team result = service.addMemberToTeam(testTeamId, testMember);

        // Then
        assertNotNull(result);
        verify(teamRepository, times(2)).save(any(Team.class)); // 元チームと新チーム
        
        ArgumentCaptor<TeamNotificationEvent> eventCaptor = ArgumentCaptor.forClass(TeamNotificationEvent.class);
        verify(notificationRepository).sendNotification(eventCaptor.capture());
        
        TeamNotificationEvent capturedEvent = eventCaptor.getValue();
        assertEquals(TeamNotificationEvent.NotificationType.TEAM_SPLIT, capturedEvent.getType());
    }

    @Test
    void addMemberToTeam_TeamNotFound_ThrowsException() {
        // Given
        when(teamRepository.findById(testTeamId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.addMemberToTeam(testTeamId, testMember)
        );
        
        assertEquals("指定されたIDのチームが見つかりません", exception.getMessage());
        verify(teamRepository).findById(testTeamId);
        verifyNoMoreInteractions(teamRepository, notificationRepository);
    }

    @Test
    void removeMemberFromTeam_NormalCase_RemovesSuccessfully() {
        // Given
        // 3名のチームから1名削除して2名にする（監視対象にならない）
        Member extraMember = createTestMember("追加メンバー", "extra@example.com");
        testTeam.addMember(extraMember);
        
        when(teamRepository.findById(testTeamId)).thenReturn(Optional.of(testTeam));
        when(teamRepository.save(testTeam)).thenReturn(testTeam);

        // When
        Team result = service.removeMemberFromTeam(testTeamId, extraMember);

        // Then
        assertNotNull(result);
        verify(teamRepository).findById(testTeamId);
        verify(teamRepository).save(testTeam);
        verifyNoInteractions(notificationRepository);
    }

    @Test
    void removeMemberFromTeam_MonitoringRequired_SendsNotification() {
        // Given
        // 3名のチームを作成し、1名削除して監視が必要な状態にする
        Member member1 = createTestMember("メンバー1", "member1@example.com");
        Member member2 = createTestMember("メンバー2", "member2@example.com");
        Member member3 = createTestMember("メンバー3", "member3@example.com");
        List<Member> threeMembers = Arrays.asList(member1, member2, member3);
        Team teamWithThreeMembers = createTestTeam("チーム", threeMembers);
        
        when(teamRepository.findById(testTeamId)).thenReturn(Optional.of(teamWithThreeMembers));
        when(teamRepository.save(teamWithThreeMembers)).thenReturn(teamWithThreeMembers);

        // When - 1名削除して2名にする（監視が必要）
        Team result = service.removeMemberFromTeam(testTeamId, member3);

        // Then
        assertNotNull(result);
        
        ArgumentCaptor<TeamNotificationEvent> eventCaptor = ArgumentCaptor.forClass(TeamNotificationEvent.class);
        verify(notificationRepository).sendNotification(eventCaptor.capture());
        
        TeamNotificationEvent capturedEvent = eventCaptor.getValue();
        assertEquals(TeamNotificationEvent.NotificationType.MONITORING_REQUIRED, capturedEvent.getType());
    }

    @Test
    void removeMemberFromTeam_RedistributionSuccess_MergesTeam() {
        // Given
        // 2名のチームを作成（削除で1名になり、合流処理が発生）
        Member member1 = createTestMember("メンバー1", "member1@example.com");
        Member member2 = createTestMember("メンバー2", "member2@example.com");
        List<Member> twoMembers = Arrays.asList(member1, member2);
        Team twoMemberTeam = createTestTeam("2名チーム", twoMembers);
        
        when(teamRepository.findById(testTeamId)).thenReturn(Optional.of(twoMemberTeam));
        
        // testTeamは3名のチーム（受け入れ可能）
        List<Team> allTeams = Arrays.asList(twoMemberTeam, testTeam);
        when(teamRepository.findAll()).thenReturn(allTeams);
        
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When - 1名削除して1名チームにし、合流させる
        Team result = service.removeMemberFromTeam(testTeamId, member2);

        // Then
        assertNotNull(result);
        verify(teamRepository, atLeastOnce()).save(any(Team.class));
        
        ArgumentCaptor<TeamNotificationEvent> eventCaptor = ArgumentCaptor.forClass(TeamNotificationEvent.class);
        verify(notificationRepository, atLeastOnce()).sendNotification(eventCaptor.capture());
    }

    @Test
    void removeMemberFromTeam_RedistributionFailure_SendsFailureNotification() {
        // Given
        // 2名のチームを作成（削除で1名になり、合流処理が発生するが失敗）
        Member member1 = createTestMember("メンバー1", "member1@example.com");
        Member member2 = createTestMember("メンバー2", "member2@example.com");
        List<Member> twoMembers = Arrays.asList(member1, member2);
        Team twoMemberTeam = createTestTeam("2名チーム", twoMembers);
        
        when(teamRepository.findById(testTeamId)).thenReturn(Optional.of(twoMemberTeam));
        
        // 合流先がない状況を作る（自分だけ）
        List<Team> allTeams = Arrays.asList(twoMemberTeam);
        when(teamRepository.findAll()).thenReturn(allTeams);
        
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When - 1名削除して合流失敗
        Team result = service.removeMemberFromTeam(testTeamId, member2);

        // Then
        assertNotNull(result);
        
        ArgumentCaptor<TeamNotificationEvent> eventCaptor = ArgumentCaptor.forClass(TeamNotificationEvent.class);
        verify(notificationRepository, atLeastOnce()).sendNotification(eventCaptor.capture());
    }

    private Member createTestMember(String name, String email) {
        return new Member(new MemberName(name), new Email(email), EnrollmentStatus.在籍中);
    }

    private Team createTestTeam(String name, List<Member> members) {
        // Teamの制約（2-4名）を満たすようにメンバーを調整
        List<Member> adjustedMembers = new ArrayList<>(members);
        if (adjustedMembers.size() < 2) {
            // 2名未満の場合はダミーメンバーを追加
            while (adjustedMembers.size() < 2) {
                adjustedMembers.add(createTestMember("ダミー" + adjustedMembers.size(), "dummy" + adjustedMembers.size() + "@example.com"));
            }
        }
        return new Team(new TeamName(name), adjustedMembers);
    }
}