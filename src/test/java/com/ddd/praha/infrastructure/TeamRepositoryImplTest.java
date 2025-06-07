package com.ddd.praha.infrastructure;

import com.ddd.praha.TestcontainersConfiguration;
import com.ddd.praha.application.repository.TeamRepository;
import com.ddd.praha.application.repository.MemberRepository;
import com.ddd.praha.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TeamRepositoryImplのテスト
 * 実際のデータベースを使用した統合テスト
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
public class TeamRepositoryImplTest {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void findAll_ShouldReturnAllTeamsWithMembers() {
        // Given: ユニークなEmailでテスト用メンバーを作成・保存
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        Member member1 = new Member(new MemberName("テスト太郎" + uniqueId), new Email("test1-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member2 = new Member(new MemberName("テスト次郎" + uniqueId), new Email("test2-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member3 = new Member(new MemberName("テスト三郎" + uniqueId), new Email("test3-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member4 = new Member(new MemberName("テスト四郎" + uniqueId), new Email("test4-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        Member savedMember3 = memberRepository.save(member3);
        Member savedMember4 = memberRepository.save(member4);

        // And: ユニークなチーム名でテスト用チームを作成・保存
        Team teamA = new Team(new TeamName("TeamA-" + uniqueId), List.of(savedMember1, savedMember2, savedMember3));
        Team teamB = new Team(new TeamName("TeamB-" + uniqueId), List.of(savedMember4, savedMember2));
        
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        // When: 全てのチームを取得
        List<Team> teams = teamRepository.findAll();

        // Then: 保存したチームが取得できること
        assertTrue(teams.size() >= 2);
        
        // And: チーム名が含まれていること
        List<String> teamNames = teams.stream()
            .map(team -> team.getName().value())
            .toList();
        assertTrue(teamNames.contains("TeamA-" + uniqueId));
        assertTrue(teamNames.contains("TeamB-" + uniqueId));

        // And: 各チームのメンバー数がドメイン制約（2-4人）を満たしていること
        Team foundTeamA = teams.stream()
            .filter(team -> ("TeamA-" + uniqueId).equals(team.getName().value()))
            .findFirst()
            .orElseThrow();
        assertEquals(3, foundTeamA.getMembers().size());  // TeamA: 3人

        Team foundTeamB = teams.stream()
            .filter(team -> ("TeamB-" + uniqueId).equals(team.getName().value()))
            .findFirst()
            .orElseThrow();
        assertEquals(2, foundTeamB.getMembers().size());  // TeamB: 2人
    }

    @Test
    void findById_WithExistingId_ShouldReturnTeamWithMembers() {
        // Given: ユニークなEmailでテスト用メンバーを作成・保存
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        Member member1 = new Member(new MemberName("テスト太郎" + uniqueId), new Email("test1-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member2 = new Member(new MemberName("テスト次郎" + uniqueId), new Email("test2-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member3 = new Member(new MemberName("テスト三郎" + uniqueId), new Email("test3-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        Member savedMember3 = memberRepository.save(member3);

        // And: ユニークなチーム名でテスト用チームを作成・保存
        Team testTeam = new Team(new TeamName("TestTeam-" + uniqueId), List.of(savedMember1, savedMember2, savedMember3));
        Team savedTeam = teamRepository.save(testTeam);
        TeamId existingId = savedTeam.getId();

        // When: IDで検索
        Optional<Team> result = teamRepository.findById(existingId);

        // Then: チームが取得できること
        assertTrue(result.isPresent());
        Team team = result.get();
        assertEquals("TestTeam-" + uniqueId, team.getName().value());

        // And: メンバーが3人いること
        assertEquals(3, team.getMembers().size());
        
        // And: メンバーの詳細が正しいこと
        List<String> memberNames = team.getMembers().stream()
            .map(member -> member.getName().value())
            .sorted()
            .toList();
        assertEquals(List.of("テスト三郎" + uniqueId, "テスト太郎" + uniqueId, "テスト次郎" + uniqueId), memberNames);
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        // Given: 存在しないチームID
        TeamId nonExistingId = new TeamId(UUID.randomUUID().toString());

        // When: IDで検索
        Optional<Team> result = teamRepository.findById(nonExistingId);

        // Then: 空のOptionalが返ること
        assertTrue(result.isEmpty());
    }

    @Test
    void save_WithNewTeam_ShouldCreateTeamWithMembers() {
        // Given: ユニークなEmailでテスト用メンバーを作成・保存
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        Member member1 = new Member(new MemberName("テスト太郎" + uniqueId), new Email("test1-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member2 = new Member(new MemberName("テスト次郎" + uniqueId), new Email("test2-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        List<Member> members = List.of(savedMember1, savedMember2);

        // And: ユニークなチーム名で新しいチーム
        Team newTeam = new Team(new TeamName("新しいチーム-" + uniqueId), members);

        // When: 保存
        Team savedTeam = teamRepository.save(newTeam);

        // Then: 保存されたチームが返ること
        assertNotNull(savedTeam);
        assertNotNull(savedTeam.getId());
        assertEquals("新しいチーム-" + uniqueId, savedTeam.getName().value());
        assertEquals(2, savedTeam.getMembers().size());

        // And: データベースから取得できること
        Optional<Team> retrieved = teamRepository.findById(savedTeam.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("新しいチーム-" + uniqueId, retrieved.get().getName().value());
        assertEquals(2, retrieved.get().getMembers().size());
    }

    @Test
    void save_WithExistingTeam_ShouldUpdateTeamAndMembers() {
        // Given: ユニークなEmailでテスト用メンバーを作成・保存
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        Member member1 = new Member(new MemberName("メンバー1-" + uniqueId), new Email("member1-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member2 = new Member(new MemberName("メンバー2-" + uniqueId), new Email("member2-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member3 = new Member(new MemberName("メンバー3-" + uniqueId), new Email("member3-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        Member savedMember3 = memberRepository.save(member3);
        
        // And: ユニークなチーム名で初期チームを作成・保存（2人）
        Team originalTeam = new Team(new TeamName("初期チーム-" + uniqueId), List.of(savedMember1, savedMember2));
        Team savedOriginalTeam = teamRepository.save(originalTeam);
        assertEquals(2, savedOriginalTeam.getMembers().size());
        
        // And: メンバーを変更（3人に増やす）
        List<Member> updatedMembers = List.of(savedMember1, savedMember2, savedMember3);
        
        // And: チーム名とメンバーを変更（リフレクションを使用してIDを設定）
        Team updatedTeam = new Team(new TeamName("更新されたチーム-" + uniqueId), updatedMembers);
        try {
            var idField = Team.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(updatedTeam, savedOriginalTeam.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set team ID", e);
        }

        // When: 保存（更新）
        Team savedTeam = teamRepository.save(updatedTeam);

        // Then: 更新されたチームが返ること
        assertEquals("更新されたチーム-" + uniqueId, savedTeam.getName().value());
        assertEquals(3, savedTeam.getMembers().size());

        // And: データベースでも更新されていること
        Optional<Team> retrieved = teamRepository.findById(savedOriginalTeam.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("更新されたチーム-" + uniqueId, retrieved.get().getName().value());
        assertEquals(3, retrieved.get().getMembers().size());
    }

    @Test
    void save_WithMemberReduction_ShouldUpdateMemberAssociation() {
        // Given: ユニークなEmailでテスト用メンバーを作成・保存
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        Member member1 = new Member(new MemberName("メンバー1-" + uniqueId), new Email("member1-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member2 = new Member(new MemberName("メンバー2-" + uniqueId), new Email("member2-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member3 = new Member(new MemberName("メンバー3-" + uniqueId), new Email("member3-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        Member savedMember3 = memberRepository.save(member3);
        
        // And: ユニークなチーム名で3人のチームを作成・保存
        Team originalTeam = new Team(new TeamName("減少テストチーム-" + uniqueId), List.of(savedMember1, savedMember2, savedMember3));
        Team savedOriginalTeam = teamRepository.save(originalTeam);
        assertEquals(3, savedOriginalTeam.getMembers().size());
        
        // And: メンバーを2人に減らす（ドメイン制約の最小数）
        List<Member> reducedMembers = List.of(savedMember1, savedMember2);
        
        Team updatedTeam = new Team(savedOriginalTeam.getName(), reducedMembers);
        try {
            var idField = Team.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(updatedTeam, savedOriginalTeam.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set team ID", e);
        }

        // When: 保存（メンバー減少）
        teamRepository.save(updatedTeam);

        // Then: データベースでメンバーが2人になっていること
        Optional<Team> retrieved = teamRepository.findById(savedOriginalTeam.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(2, retrieved.get().getMembers().size());
    }

    @Test
    void save_WithMinimumMembers_ShouldCreateValidTeam() {
        // Given: ユニークなEmailで最小2人のメンバーでチームを作成（Teamクラスの制約を考慮）
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        Member member1 = new Member(new MemberName("ミニマム1-" + uniqueId), new Email("min1-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member2 = new Member(new MemberName("ミニマム2-" + uniqueId), new Email("min2-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        
        Team newTeam = new Team(new TeamName("最小メンバーチーム-" + uniqueId), List.of(savedMember1, savedMember2));

        // When: 保存
        Team savedTeam = teamRepository.save(newTeam);

        // Then: 2人のメンバーでチームが保存されること
        assertEquals(2, savedTeam.getMembers().size());

        // And: データベースから取得しても2人であること
        Optional<Team> retrieved = teamRepository.findById(savedTeam.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(2, retrieved.get().getMembers().size());
    }

    @Test
    void save_WithMaximumMembers_ShouldCreateValidTeam() {
        // Given: ユニークなEmailで最大4人のメンバーでチームを作成（Teamクラスの制約の上限）
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        Member member1 = new Member(new MemberName("マックス1-" + uniqueId), new Email("max1-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member2 = new Member(new MemberName("マックス2-" + uniqueId), new Email("max2-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member3 = new Member(new MemberName("マックス3-" + uniqueId), new Email("max3-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member4 = new Member(new MemberName("マックス4-" + uniqueId), new Email("max4-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        Member savedMember3 = memberRepository.save(member3);
        Member savedMember4 = memberRepository.save(member4);
        
        Team newTeam = new Team(new TeamName("最大メンバーチーム-" + uniqueId), List.of(savedMember1, savedMember2, savedMember3, savedMember4));

        // When: 保存
        Team savedTeam = teamRepository.save(newTeam);

        // Then: 4人のメンバーでチームが保存されること
        assertEquals(4, savedTeam.getMembers().size());

        // And: データベースから取得しても4人であること
        Optional<Team> retrieved = teamRepository.findById(savedTeam.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(4, retrieved.get().getMembers().size());
    }
}