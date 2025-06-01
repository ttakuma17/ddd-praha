package com.ddd.praha.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TeamTest {
  private TeamName validTeamName;
  private Member member1;
  private Member member2;
  private Member member3;

  @BeforeEach
  void setUp() {
    validTeamName = new TeamName("a");
    member1 = new Member(
        new MemberName("山田太郎"),
        new Email("yamada@example.com"),
        EnrollmentStatus.在籍中
    );
    member2 = new Member(
        new MemberName("佐藤花子"),
        new Email("sato@example.com"),
        EnrollmentStatus.在籍中
    );
    member3 = new Member(
        new MemberName("鈴木一郎"),
        new Email("suzuki@example.com"),
        EnrollmentStatus.在籍中
    );
  }

  @Nested
  @DisplayName("チーム作成時のテスト")
  class CreateTeamTest {

    @Test
    @DisplayName("有効なメンバーリストでチームを作成できる")
    void createTeamWithValidMemberList() {
      List<Member> members = Arrays.asList(member1, member2);
      Team team = new Team(validTeamName, members);

      assertNotNull(team.id);
      assertEquals(validTeamName, team.name);
      assertEquals(2, team.list.size());
      assertTrue(team.list.contains(member1));
      assertTrue(team.list.contains(member2));
    }

    @Test
    @DisplayName("メンバーリストがnullの場合は例外がスローされる")
    void throwExceptionWhenMemberListIsNull() {
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        new Team(validTeamName, null);
      });
      assertEquals("メンバーリストは必須です", exception.getMessage());
    }

    @Test
    @DisplayName("メンバーリストが空の場合は例外がスローされる")
    void throwExceptionWhenMemberListIsEmpty() {
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        new Team(validTeamName, new ArrayList<>());
      });
      assertEquals("メンバーリストは必須です", exception.getMessage());
    }

    @Test
    @DisplayName("メンバーが1人の場合は例外がスローされる")
    void throwExceptionWhenOnlyOneMember() {
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        new Team(validTeamName, List.of(member1));
      });
      assertEquals("チーム人数は1名にはできません", exception.getMessage());
    }

    @Test
    @DisplayName("メンバーが5人以上の場合は例外がスローされる")
    void throwExceptionWhenMoreThanFourMembers() {
      Member member4 = new Member(
          new MemberName("田中次郎"),
          new Email("tanaka@example.com"),
          EnrollmentStatus.在籍中
      );
      Member member5 = new Member(
          new MemberName("伊藤三郎"),
          new Email("ito@example.com"),
          EnrollmentStatus.在籍中
      );

      List<Member> members = Arrays.asList(member1, member2, member3, member4, member5);

      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        new Team(validTeamName, members);
      });
      assertEquals("チームに所属できる人数は5名以上に設定することはできません", exception.getMessage());
    }
  }

  @Nested
  @DisplayName("メンバー追加のテスト")
  class AddMemberTest {

    private Team team;

    @BeforeEach
    void setUp() {
      team = new Team(validTeamName, Arrays.asList(member1, member2));
    }

    @Test
    @DisplayName("新しいメンバーをチームに追加できる")
    void addNewMemberToTeam() {
      team.addMember(member3);

      assertEquals(3, team.list.size());
      assertTrue(team.list.contains(member3));
    }

    @Test
    @DisplayName("既に所属しているメンバーを追加しようとすると例外がスローされる")
    void throwExceptionWhenAddingExistingMember() {
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        team.addMember(member1);
      });
      assertEquals("指定された参加者は既にチームに所属しています", exception.getMessage());
    }

    @Test
    @DisplayName("在籍中でないメンバーを追加しようとすると例外がスローされる")
    void throwExceptionWhenAddingNonActiveMembers() {
      Member inactiveMember = new Member(
          new MemberName("高橋健太"),
          new Email("takahashi@example.com"),
          EnrollmentStatus.休会中
      );

      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        team.addMember(inactiveMember);
      });
      assertEquals("在籍中ではない参加者はチームに追加できません", exception.getMessage());
    }
  }

  @Nested
  @DisplayName("メンバー削除のテスト")
  class DeleteMemberTest {

    private Team team;

    @BeforeEach
    void setUp() {
      team = new Team(validTeamName, Arrays.asList(member1, member2, member3));
    }

    @Test
    @DisplayName("既存のメンバーをチームから削除できる")
    void deleteExistingMember() {
      team.deleteMember(member3);

      assertEquals(2, team.list.size());
      assertFalse(team.list.contains(member3));
    }

    @Test
    @DisplayName("チームに所属していないメンバーを削除しようとすると例外がスローされる")
    void throwExceptionWhenDeletingNonExistingMember() {
      Member nonExistingMember = new Member(
          new MemberName("渡辺雄太"),
          new Email("watanabe@example.com"),
          EnrollmentStatus.在籍中
      );

      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        team.deleteMember(nonExistingMember);
      });
      assertEquals("指定された参加者はチームに所属していません", exception.getMessage());
    }
  }

  @Nested
  @DisplayName("チーム状態確認のテスト")
  class TeamStatusTest {

    @Test
    @DisplayName("メンバーが2人以下の場合はモニタリングが必要")
    void needsMonitoringWhenTwoOrLessMembers() {
      Team team = new Team(validTeamName, Arrays.asList(member1, member2));

      assertTrue(team.needsMonitoring());
    }

    @Test
    @DisplayName("メンバーが3人以上の場合はモニタリングが不要")
    void doesNotNeedMonitoringWhenMoreThanTwoMembers() {
      Team team = new Team(validTeamName, Arrays.asList(member1, member2, member3));

      assertFalse(team.needsMonitoring());
    }

    @Test
    @DisplayName("メンバーが1人の場合は再配置が必要")
    void needsRedistributionWhenOnlyOneMember() {
      Team team = new Team(validTeamName, Arrays.asList(member1, member2));
      team.deleteMember(member2);

      assertTrue(team.needsRedistribution());
    }

    @Test
    @DisplayName("メンバーが2人以上の場合は再配置が不要")
    void doesNotNeedRedistributionWhenMoreThanOneMember() {
      Team team = new Team(validTeamName, Arrays.asList(member1, member2));

      assertFalse(team.needsRedistribution());
    }

    @Test
    @DisplayName("メンバーが5人以上の場合は分割が必要")
    void needsSplittingWhenFiveOrMoreMembers() {
      Member member4 = new Member(
          new MemberName("田中次郎"),
          new Email("tanaka@example.com"),
          EnrollmentStatus.在籍中
      );
      Member member5 = new Member(
          new MemberName("伊藤三郎"),
          new Email("ito@example.com"),
          EnrollmentStatus.在籍中
      );

      // 最初に4人で作成
      Team team = new Team(validTeamName, Arrays.asList(member1, member2, member3, member4));
      // その後1人追加して5人に
      team.addMember(member5);

      assertTrue(team.needsSplitting());
    }
  }

}