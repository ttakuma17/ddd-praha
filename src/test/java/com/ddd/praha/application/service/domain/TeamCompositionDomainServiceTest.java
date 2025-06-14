package com.ddd.praha.application.service.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.ddd.praha.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
class TeamCompositionDomainServiceTest {

    private TeamCompositionDomainService service;
    private Member testMember1;
    private Member testMember2;
    private Member testMember3;
    private Member testMember4;

    @BeforeEach
    void setUp() {
        service = new TeamCompositionDomainService();

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
            new MemberName("高橋次郎"),
            new Email("takahashi@example.com"),
            EnrollmentStatus.在籍中
        );
    }

    @Test
    void executeRedistribution_合流先が見つからない場合は合流失敗フラグを返す() {
        // 準備
        Member singleMember = new Member(
            new MemberId("single-member"),
            new MemberName("単独メンバー"),
            new Email("single@example.com"),
            EnrollmentStatus.在籍中
        );
        
        Team singleTeam = new Team(
            new TeamId("team-single"),
            new TeamName("一名チーム"),
            Arrays.asList(singleMember, testMember1)
        );

        // 4名の満員チーム（合流できない）
        Member fullMember1 = new Member(
            new MemberId("full-member-1"),
            new MemberName("満員メンバー1"),
            new Email("full1@example.com"),
            EnrollmentStatus.在籍中
        );
        Member fullMember2 = new Member(
            new MemberId("full-member-2"),
            new MemberName("満員メンバー2"),
            new Email("full2@example.com"),
            EnrollmentStatus.在籍中
        );
        Member fullMember3 = new Member(
            new MemberId("full-member-3"),
            new MemberName("満員メンバー3"),
            new Email("full3@example.com"),
            EnrollmentStatus.在籍中
        );
        Member fullMember4 = new Member(
            new MemberId("full-member-4"),
            new MemberName("満員メンバー4"),
            new Email("full4@example.com"),
            EnrollmentStatus.在籍中
        );
        
        Team fullTeam = new Team(
            new TeamId("team-full"),
            new TeamName("満員チーム"),
            Arrays.asList(fullMember1, fullMember2, fullMember3, fullMember4)
        );

        List<Team> allTeams = new ArrayList<>(Arrays.asList(singleTeam, fullTeam));

        // 実行 - testMember1を削除して1名チームにする
        TeamRedistributionResult result = service.executeRedistribution(singleTeam, testMember1, allTeams);

        // 検証
        assertTrue(result.mergeFailure());
        assertFalse(result.requiresMerge());
        assertFalse(result.requiresMonitoring());
        assertEquals(singleMember, result.removedMember()); // 残ったメンバーが返される
    }

    @Test
    void executeRedistribution_合流が成功する場合は合流フラグを返す() {
        // 準備
        Member singleMember = new Member(
            new MemberId("single-member"),
            new MemberName("単独メンバー"),
            new Email("single@example.com"),
            EnrollmentStatus.在籍中
        );
        
        Team singleTeam = new Team(
            new TeamId("team-single"),
            new TeamName("一名チーム"),
            Arrays.asList(singleMember, testMember1)
        );

        Team targetTeam = new Team(
            new TeamId("team-target"),
            new TeamName("合流先チーム"),
            Arrays.asList(testMember2, testMember3)
        );

        List<Team> allTeams = new ArrayList<>(Arrays.asList(singleTeam, targetTeam));

        // 実行 - testMember1を削除して1名チームにする
        TeamRedistributionResult result = service.executeRedistribution(singleTeam, testMember1, allTeams);

        // 検証
        assertTrue(result.requiresMerge());
        assertFalse(result.mergeFailure());
        assertEquals(testMember1, result.removedMember());
        // 合流先チームは3名になっている
        assertEquals(3, result.composition().getOriginalTeam().getMembers().size());
    }

    @Test
    void executeRedistribution_2名以下になった場合は監視フラグを返す() {
        // 準備
        Team threeTeam = new Team(
            new TeamId("team-three"),
            new TeamName("三名チーム"),
            Arrays.asList(testMember1, testMember2, testMember3)
        );

        List<Team> allTeams = new ArrayList<>(Arrays.asList(threeTeam));

        // 実行 - メンバー1を削除して2名チームにする
        TeamRedistributionResult result = service.executeRedistribution(threeTeam, testMember1, allTeams);

        // 検証
        assertTrue(result.requiresMonitoring());
        assertFalse(result.requiresMerge());
        assertFalse(result.mergeFailure());
        assertEquals(testMember1, result.removedMember());
        assertEquals(2, result.composition().getOriginalTeam().getMembers().size());
    }

    @Test
    void assignMemberToTeam_復帰したメンバーを最も人数が少ないチームに割り当てる() {
        // 準備
        Member team2Member1 = new Member(
            new MemberId("team2-member-1"),
            new MemberName("チーム2メンバー1"),
            new Email("team2-1@example.com"),
            EnrollmentStatus.在籍中
        );
        Member team2Member2 = new Member(
            new MemberId("team2-member-2"),
            new MemberName("チーム2メンバー2"),
            new Email("team2-2@example.com"),
            EnrollmentStatus.在籍中
        );
        
        Team team2Members = new Team(
            new TeamId("team-002"),
            new TeamName("二名チーム"),
            Arrays.asList(team2Member1, team2Member2)
        );

        Team team3Members = new Team(
            new TeamId("team-003"),
            new TeamName("三名チーム"),
            Arrays.asList(testMember1, testMember2, testMember3)
        );

        List<Team> allTeams = new ArrayList<>(Arrays.asList(team2Members, team3Members));

        // 実行
        TeamCompositionResult result = service.assignMemberToTeam(testMember4, allTeams);

        // 検証
        assertFalse(result.requiresSplit());
        assertEquals(team2Members, result.composition().getOriginalTeam());
        assertEquals(3, result.composition().getOriginalTeam().getMembers().size()); // 2名 + 1名 = 3名
    }

    @Test
    void assignMemberToTeam_5名になる場合はチーム分割を実行する() {
        // 準備
        Member team4Member1 = new Member(
            new MemberId("team4-member-1"),
            new MemberName("チーム4メンバー1"),
            new Email("team4-1@example.com"),
            EnrollmentStatus.在籍中
        );
        Member team4Member2 = new Member(
            new MemberId("team4-member-2"),
            new MemberName("チーム4メンバー2"),
            new Email("team4-2@example.com"),
            EnrollmentStatus.在籍中
        );
        Member team4Member3 = new Member(
            new MemberId("team4-member-3"),
            new MemberName("チーム4メンバー3"),
            new Email("team4-3@example.com"),
            EnrollmentStatus.在籍中
        );
        Member team4Member4 = new Member(
            new MemberId("team4-member-4"),
            new MemberName("チーム4メンバー4"),
            new Email("team4-4@example.com"),
            EnrollmentStatus.在籍中
        );
        
        Team team4Members = new Team(
            new TeamId("team-004"),
            new TeamName("四名チーム"),
            Arrays.asList(team4Member1, team4Member2, team4Member3, team4Member4)
        );

        List<Team> allTeams = new ArrayList<>(Arrays.asList(team4Members));

        Member newMember = new Member(
            new MemberId("member-005"),
            new MemberName("伊藤三郎"),
            new Email("ito@example.com"),
            EnrollmentStatus.在籍中
        );

        // 実行
        TeamCompositionResult result = service.assignMemberToTeam(newMember, allTeams);

        // 検証
        assertTrue(result.requiresSplit());
        assertNotNull(result.composition().getNewTeam());
        assertEquals(2, result.composition().getOriginalTeam().getMembers().size());
        assertEquals(3, result.composition().getNewTeam().getMembers().size());
    }

    @Test
    void assignMemberToTeam_割り当て可能なチームがない場合は例外をスローする() {
        // 準備 - 空のチームリスト
        List<Team> allTeams = new ArrayList<>();

        Member newMember = new Member(
            new MemberId("member-005"),
            new MemberName("伊藤三郎"),
            new Email("ito@example.com"),
            EnrollmentStatus.在籍中
        );

        // 実行・検証
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> service.assignMemberToTeam(newMember, allTeams)
        );

        assertEquals("メンバーを割り当て可能なチームが見つかりません", exception.getMessage());
    }
}