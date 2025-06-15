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
        new TeamName("テストチーム1"),
        Arrays.asList(testMember1, testMember2)
    );
    Team team2 = new Team(
        new TeamId(teamId2),
        new TeamName("テストチーム2"),
        List.of(testMember3)
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
        new TeamName("テストチーム"),
        Arrays.asList(testMember1, testMember2)
    );
    teamRepository.create(team);

    // 実行
    Team result = teamRepository.get(new TeamId("team-001"));

    // 検証
    assertAll(
        () -> assertEquals("team-001", result.getId().value()),
        () -> assertEquals("テストチーム", result.getName().value()),
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
        new TeamName("新規チーム"),
        Arrays.asList(testMember1, testMember2)
    );

    // 実行
    teamRepository.create(newTeam);

    // 検証
    Team savedTeam = teamRepository.get(new TeamId("new-team"));
    assertAll(
        () -> assertEquals("new-team", savedTeam.getId().value()),
        () -> assertEquals("新規チーム", savedTeam.getName().value()),
        () -> assertEquals(2, savedTeam.getMembers().size())
    );
  }

  @Test
  void 同じチームIDのレコードが作成される場合は例外() {
    // 準備
    Team team = new Team(
        new TeamId("duplicate-team"),
        new TeamName("重複チーム"),
        Arrays.asList(testMember1, testMember2)
    );
    teamRepository.create(team);

    // 同じIDで別のチームを作成
    Team duplicateTeam = new Team(
        new TeamId("duplicate-team"),
        new TeamName("重複チーム2"),
        List.of(testMember3)
    );

    // 実行・検証
    assertThrows(
        IllegalStateException.class,
        () -> teamRepository.create(duplicateTeam)
    );
  }

  @Test
  void メンバーが空のチームを作成できる() {
    // 準備
    Team emptyTeam = new Team(
        new TeamId("empty-team"),
        new TeamName("空チーム"),
        List.of()
    );

    // 実行
    teamRepository.create(emptyTeam);

    // 検証
    Team savedTeam = teamRepository.get(new TeamId("empty-team"));
    assertAll(
        () -> assertEquals("empty-team", savedTeam.getId().value()),
        () -> assertEquals("空チーム", savedTeam.getName().value()),
        () -> assertTrue(savedTeam.getMembers().isEmpty())
    );
  }

  @Test
  void チームにメンバーを追加できる() {
    // 準備
    Team team = new Team(
        new TeamId("test-team"),
        new TeamName("テストチーム"),
        List.of(testMember1)
    );
    teamRepository.create(team);

    // 実行
    teamRepository.addMember(new TeamId("test-team"), testMember2.getId());

    // 検証
    Team updatedTeam = teamRepository.get(new TeamId("test-team"));
    assertAll(
        () -> assertEquals(2, updatedTeam.getMembers().size()),
        () -> assertTrue(updatedTeam.getMembers().stream()
            .anyMatch(m -> m.getId().value().equals(testMember2.getId().value())))
    );
  }

  @Test
  void チームからメンバーを外せる() {
    // 準備
    Team team = new Team(
        new TeamId("test-team"),
        new TeamName("テストチーム"),
        Arrays.asList(testMember1, testMember2)
    );
    teamRepository.create(team);

    // 実行
    teamRepository.removeMember(new TeamId("test-team"), testMember1.getId());

    // 検証
    Team updatedTeam = teamRepository.get(new TeamId("test-team"));
    assertAll(
        () -> assertEquals(1, updatedTeam.getMembers().size()),
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
        new TeamName("削除予定チーム"),
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