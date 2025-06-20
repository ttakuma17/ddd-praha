package com.ddd.praha.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import com.ddd.praha.annotation.MyBatisRepositoryTest;
import com.ddd.praha.application.repository.MemberRepository;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.Team;
import com.ddd.praha.domain.model.Email;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.model.MemberName;
import com.ddd.praha.domain.model.TeamId;
import com.ddd.praha.domain.model.TeamName;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@MyBatisRepositoryTest
class TeamRepositoryImplTest {

  @Autowired
  private TeamRepositoryImpl teamRepository;

  @Autowired
  private MemberRepository memberRepository;

  private Member testMember1;
  private Member testMember2;
  private Member testMember3;

  @BeforeEach
  void setUp() {
    // テスト用メンバーの作成（動的ID使用）
    testMember1 = new Member(
        new MemberId("member-" + UUID.randomUUID()),
        new MemberName("田中太郎"),
        new Email("tanaka-" + UUID.randomUUID() + "@example.com"),
        EnrollmentStatus.在籍中
    );

    testMember2 = new Member(
        new MemberId("member-" + UUID.randomUUID()),
        new MemberName("佐藤花子"),
        new Email("sato-" + UUID.randomUUID() + "@example.com"),
        EnrollmentStatus.在籍中
    );

    testMember3 = new Member(
        new MemberId("member-" + UUID.randomUUID()),
        new MemberName("鈴木一郎"),
        new Email("suzuki-" + UUID.randomUUID() + "@example.com"),
        EnrollmentStatus.在籍中
    );

    // テスト用メンバーをデータベースに保存
    memberRepository.save(testMember1);
    memberRepository.save(testMember2);
    memberRepository.save(testMember3);
  }

  @Test
  void チーム一覧を取得できる() {
    // 準備
    String teamId1 = "team-" + UUID.randomUUID();
    String teamId2 = "team-" + UUID.randomUUID();
    
    Team team1 = new Team(
        new TeamId(teamId1),
        new TeamName("TeamAlpha"),
        Arrays.asList(testMember1, testMember2)
    );
    // テスト用メンバー4を新規作成
    Member testMember4 = new Member(
        new MemberId("member-" + UUID.randomUUID()),
        new MemberName("山田次郎"),
        new Email("yamada-" + UUID.randomUUID() + "@example.com"),
        EnrollmentStatus.在籍中
    );
    memberRepository.save(testMember4);
    
    Team team2 = new Team(
        new TeamId(teamId2),
        new TeamName("TeamBeta"),
        Arrays.asList(testMember3, testMember4)
    );
    
    teamRepository.create(team1);
    teamRepository.create(team2);

    // 実行
    List<Team> teams = teamRepository.getAll();

    // 検証
    assertAll(
        () -> assertTrue(teams.size() >= 2),
        () -> assertTrue(teams.stream().anyMatch(t -> t.getId().value().equals(teamId1))),
        () -> assertTrue(teams.stream().anyMatch(t -> t.getId().value().equals(teamId2)))
    );
  }

  @Test
  void チーム一覧を取得できなかった場合は空のリストを返す() {
    // 実行
    List<Team> teams = teamRepository.getAll();

    // 検証
    assertTrue(teams.isEmpty());
  }

  @Test
  void チームを検索できる() {
    // 準備
    Team team = new Team(
        new TeamId("team-001"),
        new TeamName("TestTeam"),
        Arrays.asList(testMember1, testMember2)
    );
    teamRepository.create(team);

    // 実行
    Team result = teamRepository.get(new TeamId("team-001"));

    // 検証
    assertAll(
        () -> assertEquals("team-001", result.getId().value()),
        () -> assertEquals("TestTeam", result.getName().value()),
        () -> assertEquals(2, result.getMembers().size()),
        () -> assertTrue(result.getMembers().stream()
            .anyMatch(m -> m.getId().value().equals(testMember1.getId().value()))),
        () -> assertTrue(result.getMembers().stream()
            .anyMatch(m -> m.getId().value().equals(testMember2.getId().value())))
    );
  }

  @Test
  void チームが存在しなかった場合は例外() {
    // 実行・検証
    assertThrows(
        IllegalStateException.class,
        () -> teamRepository.get(new TeamId("non-existing-team"))
    );
  }

  @Test
  void チームを作成できる() {
    // 準備
    Team newTeam = new Team(
        new TeamId("new-team"),
        new TeamName("NewTeam"),
        Arrays.asList(testMember1, testMember2)
    );

    // 実行
    teamRepository.create(newTeam);

    // 検証
    Team savedTeam = teamRepository.get(new TeamId("new-team"));
    assertAll(
        () -> assertEquals("new-team", savedTeam.getId().value()),
        () -> assertEquals("NewTeam", savedTeam.getName().value()),
        () -> assertEquals(2, savedTeam.getMembers().size())
    );
  }

  @Test
  void 同じチームIDのレコードが作成される場合は例外() {
    // 準備
    Team team = new Team(
        new TeamId("duplicate-team"),
        new TeamName("DuplicateTeam"),
        Arrays.asList(testMember1, testMember2)
    );
    teamRepository.create(team);

    // 同じIDで別のチームを作成
    // 新しいテストメンバーを作成
    Member testMember5 = new Member(
        new MemberId("member-" + UUID.randomUUID()),
        new MemberName("高橋五郎"),
        new Email("takahashi-" + UUID.randomUUID() + "@example.com"),
        EnrollmentStatus.在籍中
    );
    memberRepository.save(testMember5);
    
    Team duplicateTeam = new Team(
        new TeamId("duplicate-team"),
        new TeamName("DuplicateTeamTwo"),
        Arrays.asList(testMember3, testMember5)
    );

    // 実行・検証
    assertThrows(
        IllegalStateException.class,
        () -> teamRepository.create(duplicateTeam)
    );
  }

  @Test
  void 最小メンバー数のチームを作成できる() {
    // 準備：最小メンバー数（2名）のチーム
    Team minTeam = new Team(
        new TeamId("min-team"),
        new TeamName("MinTeam"),
        Arrays.asList(testMember1, testMember2)
    );

    // 実行
    teamRepository.create(minTeam);

    // 検証
    Team savedTeam = teamRepository.get(new TeamId("min-team"));
    assertAll(
        () -> assertEquals("min-team", savedTeam.getId().value()),
        () -> assertEquals("MinTeam", savedTeam.getName().value()),
        () -> assertEquals(2, savedTeam.getMembers().size())
    );
  }

  @Test
  void チームにメンバーを追加できる() {
    // 準備
    Team team = new Team(
        new TeamId("test-team"),
        new TeamName("TestTeam"),
        Arrays.asList(testMember1, testMember2)
    );
    teamRepository.create(team);

    // 実行
    teamRepository.addMember(new TeamId("test-team"), testMember3.getId());

    // 検証
    Team updatedTeam = teamRepository.get(new TeamId("test-team"));
    assertAll(
        () -> assertEquals(3, updatedTeam.getMembers().size()),
        () -> assertTrue(updatedTeam.getMembers().stream()
            .anyMatch(m -> m.getId().value().equals(testMember3.getId().value())))
    );
  }

  @Test
  void チームからメンバーを外せる() {
    // 準備：3名のチームを作成して1名削除後も2名以上を維持
    Team team = new Team(
        new TeamId("test-team"),
        new TeamName("TestTeam"),
        Arrays.asList(testMember1, testMember2, testMember3)
    );
    teamRepository.create(team);

    // 実行
    teamRepository.removeMember(new TeamId("test-team"), testMember1.getId());

    // 検証
    Team updatedTeam = teamRepository.get(new TeamId("test-team"));
    assertAll(
        () -> assertEquals(2, updatedTeam.getMembers().size()),
        () -> assertFalse(updatedTeam.getMembers().stream()
            .anyMatch(m -> m.getId().value().equals(testMember1.getId().value()))),
        () -> assertTrue(updatedTeam.getMembers().stream()
            .anyMatch(m -> m.getId().value().equals(testMember2.getId().value())))
    );
  }

  @Test
  void チームを削除できる() {
    // 準備
    Team team = new Team(
        new TeamId("delete-team"),
        new TeamName("ToBeDeletedTeam"),
        Arrays.asList(testMember1, testMember2)
    );
    teamRepository.create(team);

    // 削除前の確認
    Team beforeDelete = teamRepository.get(new TeamId("delete-team"));
    assertNotNull(beforeDelete);

    // 実行
    teamRepository.delete(team);

    // 検証 - チームが取得できないことを確認
    assertThrows(
        IllegalStateException.class,
        () -> teamRepository.get(new TeamId("delete-team"))
    );
  }
}