package com.ddd.praha.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TeamTest {

    private Team team;
    private Member testMember;

    @BeforeEach
    void setUp() {
        List<Member> members = Arrays.asList(
            createTestMember("メンバー1", "member1@example.com"),
            createTestMember("メンバー2", "member2@example.com"),
            createTestMember("メンバー3", "member3@example.com")
        );
        team = new Team(new TeamName("テストチーム"), members);
        testMember = createTestMember("新メンバー", "new@example.com");
    }

    @Test
    void addMemberWithComposition_NormalCase_ReturnsNoChange() {
        // When
        TeamComposition result = team.addMemberWithComposition(testMember);

        // Then
        assertEquals(TeamComposition.CompositionType.NO_CHANGE, result.getType());
        assertEquals(team, result.getOriginalTeam());
        assertNull(result.getNewTeam());
        assertTrue(result.getMovedMembers().isEmpty());
        assertEquals(4, team.getMembers().size());
    }

    @Test
    void addMemberWithComposition_FourMembersTeam_ReturnsSplit() {
        // Given
        Member fourthMember = createTestMember("4番目", "fourth@example.com");
        team.addMember(fourthMember);

        // When
        TeamComposition result = team.addMemberWithComposition(testMember);

        // Then
        assertEquals(TeamComposition.CompositionType.SPLIT, result.getType());
        assertEquals(team, result.getOriginalTeam());
        assertNotNull(result.getNewTeam());
        assertEquals("テストチーム-分割", result.getNewTeam().getName().value());
        assertEquals(2, result.getOriginalTeam().getMembers().size());
        assertEquals(3, result.getNewTeam().getMembers().size());
        assertEquals(3, result.getMovedMembers().size());
    }

    @Test
    void mergeWithOtherTeam_WithAvailableTarget_ReturnsMerge() {
        // Given
        // 1名のチームを作成
        Member singleMember = createTestMember("単独", "single@example.com");
        Member dummyMember = createTestMember("ダミー", "dummy@example.com");
        Team singleTeam = new Team(new TeamName("単独チーム"), Arrays.asList(singleMember, dummyMember));
        singleTeam.deleteMember(dummyMember);

        List<Team> allTeams = Arrays.asList(singleTeam, team);

        // When
        Optional<TeamComposition> result = singleTeam.mergeWithOtherTeam(allTeams);

        // Then
        assertTrue(result.isPresent());
        TeamComposition composition = result.get();
        assertEquals(TeamComposition.CompositionType.MERGE, composition.getType());
        assertEquals(team, composition.getOriginalTeam());
        assertEquals(4, composition.getOriginalTeam().getMembers().size());
        assertEquals(1, composition.getMovedMembers().size());
        assertEquals(singleMember, composition.getMovedMembers().get(0));
    }

    @Test
    void mergeWithOtherTeam_WithNoAvailableTarget_ReturnsEmpty() {
        // Given
        // 1名のチームを作成
        Member singleMember = createTestMember("単独", "single@example.com");
        Member dummyMember = createTestMember("ダミー", "dummy@example.com");
        Team singleTeam = new Team(new TeamName("単独チーム"), Arrays.asList(singleMember, dummyMember));
        singleTeam.deleteMember(dummyMember);

        // 4名のフルチームを作成
        List<Member> fullMembers = Arrays.asList(
            createTestMember("フル1", "full1@example.com"),
            createTestMember("フル2", "full2@example.com"),
            createTestMember("フル3", "full3@example.com"),
            createTestMember("フル4", "full4@example.com")
        );
        Team fullTeam = new Team(new TeamName("フルチーム"), fullMembers);

        List<Team> allTeams = Arrays.asList(singleTeam, fullTeam);

        // When
        Optional<TeamComposition> result = singleTeam.mergeWithOtherTeam(allTeams);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void mergeWithOtherTeam_NotSingleMemberTeam_ThrowsException() {
        // Given
        List<Team> allTeams = Arrays.asList(team);

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> team.mergeWithOtherTeam(allTeams)
        );
        
        assertEquals("合流は1名のチームのみ可能です", exception.getMessage());
    }

    @Test
    void canAcceptNewMember_ThreeMemberTeam_ReturnsTrue() {
        // When & Then
        assertTrue(team.canAcceptNewMember());
    }

    @Test
    void canAcceptNewMember_FourMemberTeam_ReturnsFalse() {
        // Given
        Member fourthMember = createTestMember("4番目", "fourth@example.com");
        team.addMember(fourthMember);

        // When & Then
        assertFalse(team.canAcceptNewMember());
    }

    private Member createTestMember(String name, String email) {
        return new Member(new MemberName(name), new Email(email), EnrollmentStatus.在籍中);
    }
}