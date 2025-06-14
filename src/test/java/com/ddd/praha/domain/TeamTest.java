package com.ddd.praha.domain;

import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.Team;
import com.ddd.praha.domain.model.Email;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberName;
import com.ddd.praha.domain.model.TeamComposition;
import com.ddd.praha.domain.model.TeamId;
import com.ddd.praha.domain.model.TeamName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

class TeamTest {

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = createTestMember("新メンバー", "new@example.com");
    }
    
    private Team createTestTeam() {
        List<Member> members = Arrays.asList(
            createTestMember("メンバー1", "member1@example.com"),
            createTestMember("メンバー2", "member2@example.com"),
            createTestMember("メンバー3", "member3@example.com")
        );
        return new Team(new TeamName("テストチーム"), members);
    }

    @Test
    void addMemberWithComposition_NormalCase_ReturnsNoChange() {
        // Given
        Team team = createTestTeam();
        
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
        Team team = createTestTeam();
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

        Team team = createTestTeam();
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
        Team team = createTestTeam();
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
        // Given
        Team team = createTestTeam();
        
        // When & Then
        assertTrue(team.canAcceptNewMember());
    }

    @Test
    void canAcceptNewMember_FourMemberTeam_ReturnsFalse() {
        // Given
        Team team = createTestTeam();
        Member fourthMember = createTestMember("4番目", "fourth@example.com");
        team.addMember(fourthMember);

        // When & Then
        assertFalse(team.canAcceptNewMember());
    }

    @Test
    void findSmallestTeam_最も人数が少ないチームを見つけることができる() {
        // 準備
        Team team2Members = new Team(
            new TeamId("team-002"),
            new TeamName("二名チーム"),
            Arrays.asList(
                createTestMember("メンバー1", "member1@example.com"),
                createTestMember("メンバー2", "member2@example.com")
            )
        );

        Team team = createTestTeam();
        List<Team> teams = Arrays.asList(team, team2Members);

        // 実行
        Optional<Team> result = Team.findSmallestTeam(teams);

        // 検証
        assertTrue(result.isPresent());
        assertEquals("team-002", result.get().getId().value());
        assertEquals(2, result.get().getMembers().size());
    }

    @Test
    void findSmallestTeam_同じ人数のチームが複数ある場合はランダムに選択される() {
        // 準備
        Team team1 = new Team(
            new TeamId("team-001"),
            new TeamName("チーム1"),
            Arrays.asList(
                createTestMember("メンバー1", "member1@example.com"),
                createTestMember("メンバー2", "member2@example.com")
            )
        );

        Team team2 = new Team(
            new TeamId("team-002"),
            new TeamName("チーム2"),
            Arrays.asList(
                createTestMember("メンバー3", "member3@example.com"),
                createTestMember("メンバー4", "member4@example.com")
            )
        );

        List<Team> teams = Arrays.asList(team1, team2);

        // ランダム性を確認するため複数回実行
        boolean foundTeam1 = false;
        boolean foundTeam2 = false;

        for (int i = 0; i < 50; i++) {
            Optional<Team> result = Team.findSmallestTeam(teams);
            assertTrue(result.isPresent());
            
            String teamId = result.get().getId().value();
            if (teamId.equals("team-001")) {
                foundTeam1 = true;
            } else if (teamId.equals("team-002")) {
                foundTeam2 = true;
            }
            
            // 両方のチームが選ばれたことを確認できたら終了
            if (foundTeam1 && foundTeam2) {
                break;
            }
        }

        // 少なくとも一度は両方のチームが選ばれることを確認
        assertTrue(foundTeam1 || foundTeam2, "ランダム選択が動作していません");
    }

    @Test
    void findSmallestTeam_合流可能なチームがない場合は空のOptionalを返す() {
        // 準備 - 4名のチーム（合流できない）
        Team fullTeam = new Team(
            new TeamId("team-001"),
            new TeamName("満員チーム"),
            Arrays.asList(
                createTestMember("メンバー1", "member1@example.com"),
                createTestMember("メンバー2", "member2@example.com"),
                createTestMember("メンバー3", "member3@example.com"),
                createTestMember("メンバー4", "member4@example.com")
            )
        );

        List<Team> teams = List.of(fullTeam);

        // 実行
        Optional<Team> result = Team.findSmallestTeam(teams);

        // 検証
        assertTrue(result.isEmpty());
    }

    @Test
    void mergeWithOtherTeam_同じ人数のチームが複数ある場合はランダム選択される() {
        // 準備
        Member singleMember = createTestMember("単独", "single@example.com");
        Member dummyMember = createTestMember("ダミー", "dummy@example.com");
        Team singleTeam = new Team(new TeamName("単独チーム"), Arrays.asList(singleMember, dummyMember));
        singleTeam.deleteMember(dummyMember);

        Team team2Members1 = new Team(
            new TeamId("team-001"),
            new TeamName("二名チーム1"),
            Arrays.asList(
                createTestMember("メンバー1", "member1@example.com"),
                createTestMember("メンバー2", "member2@example.com")
            )
        );

        Team team2Members2 = new Team(
            new TeamId("team-002"),
            new TeamName("二名チーム2"),
            Arrays.asList(
                createTestMember("メンバー3", "member3@example.com"),
                createTestMember("メンバー4", "member4@example.com")
            )
        );

        List<Team> allTeams = Arrays.asList(singleTeam, team2Members1, team2Members2);

        // ランダム性を確認するため複数回実行
        boolean foundTeam1 = false;
        boolean foundTeam2 = false;

        for (int i = 0; i < 50; i++) {
            // 新しいインスタンスを作成（前のテストの状態をリセット）
            Member freshSingleMember = createTestMember("単独", "single@example.com");
            Member freshDummyMember = createTestMember("ダミー", "dummy@example.com");
            Team freshSingleTeam = new Team(new TeamName("単独チーム"), Arrays.asList(freshSingleMember, freshDummyMember));
            freshSingleTeam.deleteMember(freshDummyMember);

            Team freshTeam1 = new Team(
                new TeamId("team-001"),
                new TeamName("二名チーム1"),
                Arrays.asList(
                    createTestMember("メンバー1", "member1@example.com"),
                    createTestMember("メンバー2", "member2@example.com")
                )
            );

            Team freshTeam2 = new Team(
                new TeamId("team-002"),
                new TeamName("二名チーム2"),
                Arrays.asList(
                    createTestMember("メンバー3", "member3@example.com"),
                    createTestMember("メンバー4", "member4@example.com")
                )
            );

            List<Team> freshAllTeams = Arrays.asList(freshSingleTeam, freshTeam1, freshTeam2);

            Optional<TeamComposition> result = freshSingleTeam.mergeWithOtherTeam(freshAllTeams);
            assertTrue(result.isPresent());

            String mergedTeamId = result.get().getOriginalTeam().getId().value();
            if (mergedTeamId.equals("team-001")) {
                foundTeam1 = true;
            } else if (mergedTeamId.equals("team-002")) {
                foundTeam2 = true;
            }

            // 両方のチームが選ばれたことを確認できたら終了
            if (foundTeam1 && foundTeam2) {
                break;
            }
        }

        // 少なくとも一度は両方のチームが選ばれることを確認
        assertTrue(foundTeam1 || foundTeam2, "合流時のランダム選択が動作していません");
    }

    private Member createTestMember(String name, String email) {
        return new Member(new MemberName(name), new Email(email), EnrollmentStatus.在籍中);
    }
}