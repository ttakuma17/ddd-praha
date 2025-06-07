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

import static org.assertj.core.api.Assertions.assertThat;

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
        // Given: Flywayマイグレーションでテストデータが投入されている

        // When: 全てのチームを取得
        List<Team> teams = teamRepository.findAll();

        // Then: 2つのチームが取得できること
        assertThat(teams).hasSize(2);
        
        // And: チーム名が正しいこと
        assertThat(teams)
            .extracting(team -> team.getName().value())
            .containsExactlyInAnyOrder("TeamA", "TeamB");

        // And: 各チームのメンバー数がドメイン制約（2-4人）を満たしていること
        Team teamA = teams.stream()
            .filter(team -> "TeamA".equals(team.getName().value()))
            .findFirst()
            .orElseThrow();
        assertThat(teamA.getMembers()).hasSize(3);  // TeamA: 3人

        Team teamB = teams.stream()
            .filter(team -> "TeamB".equals(team.getName().value()))
            .findFirst()
            .orElseThrow();
        assertThat(teamB.getMembers()).hasSize(2);  // TeamB: 2人
    }

    @Test
    void findById_WithExistingId_ShouldReturnTeamWithMembers() {
        // Given: 既存のチームID（TeamA）
        TeamId existingId = new TeamId("660e8400-e29b-41d4-a716-446655440001");

        // When: IDで検索
        Optional<Team> result = teamRepository.findById(existingId);

        // Then: チームが取得できること
        assertThat(result).isPresent();
        Team team = result.get();
        assertThat(team.getName().value()).isEqualTo("TeamA");

        // And: メンバーが3人いること（更新されたテストデータに合わせて）
        assertThat(team.getMembers()).hasSize(3);
        
        // And: メンバーの詳細が正しいこと
        List<String> memberNames = team.getMembers().stream()
            .map(member -> member.getName().value())
            .toList();
        assertThat(memberNames).containsExactlyInAnyOrder("テスト太郎", "テスト次郎", "テスト三郎");
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        // Given: 存在しないチームID
        TeamId nonExistingId = new TeamId(UUID.randomUUID().toString());

        // When: IDで検索
        Optional<Team> result = teamRepository.findById(nonExistingId);

        // Then: 空のOptionalが返ること
        assertThat(result).isEmpty();
    }

    @Test
    void save_WithNewTeam_ShouldCreateTeamWithMembers() {
        // Given: メンバーを取得
        Member member1 = memberRepository.findById(new MemberId("550e8400-e29b-41d4-a716-446655440001")).orElseThrow();
        Member member2 = memberRepository.findById(new MemberId("550e8400-e29b-41d4-a716-446655440002")).orElseThrow();
        List<Member> members = List.of(member1, member2);

        // And: 新しいチーム
        Team newTeam = new Team(new TeamName("新しいチーム"), members);

        // When: 保存
        Team savedTeam = teamRepository.save(newTeam);

        // Then: 保存されたチームが返ること
        assertThat(savedTeam).isNotNull();
        assertThat(savedTeam.getId()).isNotNull();
        assertThat(savedTeam.getName().value()).isEqualTo("新しいチーム");
        assertThat(savedTeam.getMembers()).hasSize(2);

        // And: データベースから取得できること
        Optional<Team> retrieved = teamRepository.findById(savedTeam.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName().value()).isEqualTo("新しいチーム");
        assertThat(retrieved.get().getMembers()).hasSize(2);
    }

    @Test
    void save_WithExistingTeam_ShouldUpdateTeamAndMembers() {
        // Given: 既存のチーム（TeamB、2人）を取得
        TeamId existingId = new TeamId("660e8400-e29b-41d4-a716-446655440002");
        Team existingTeam = teamRepository.findById(existingId).orElseThrow();
        assertThat(existingTeam.getMembers()).hasSize(2);
        
        // And: メンバーを変更（3人に増やす）
        Member additionalMember = memberRepository.findById(new MemberId("550e8400-e29b-41d4-a716-446655440001")).orElseThrow();
        List<Member> originalMembers = existingTeam.getMembers();
        List<Member> updatedMembers = List.of(
            originalMembers.get(0),
            originalMembers.get(1), 
            additionalMember
        );
        
        // And: チーム名とメンバーを変更（リフレクションを使用してIDを設定）
        Team updatedTeam = new Team(new TeamName("更新されたチーム"), updatedMembers);
        try {
            var idField = Team.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(updatedTeam, existingId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set team ID", e);
        }

        // When: 保存（更新）
        Team savedTeam = teamRepository.save(updatedTeam);

        // Then: 更新されたチームが返ること
        assertThat(savedTeam.getName().value()).isEqualTo("更新されたチーム");
        assertThat(savedTeam.getMembers()).hasSize(3);

        // And: データベースでも更新されていること
        Optional<Team> retrieved = teamRepository.findById(existingId);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName().value()).isEqualTo("更新されたチーム");
        assertThat(retrieved.get().getMembers()).hasSize(3);
    }

    @Test
    void save_WithMemberReduction_ShouldUpdateMemberAssociation() {
        // Given: 既存のチーム（TeamA、3人）を2人に減らす
        TeamId existingId = new TeamId("660e8400-e29b-41d4-a716-446655440001");
        Team existingTeam = teamRepository.findById(existingId).orElseThrow();
        assertThat(existingTeam.getMembers()).hasSize(3);
        
        // And: メンバーを2人に減らす（ドメイン制約の最小数）
        List<Member> reducedMembers = List.of(
            existingTeam.getMembers().get(0),
            existingTeam.getMembers().get(1)
        );
        
        Team updatedTeam = new Team(existingTeam.getName(), reducedMembers);
        try {
            var idField = Team.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(updatedTeam, existingId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set team ID", e);
        }

        // When: 保存（メンバー減少）
        teamRepository.save(updatedTeam);

        // Then: データベースでメンバーが2人になっていること
        Optional<Team> retrieved = teamRepository.findById(existingId);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getMembers()).hasSize(2);
    }

    @Test
    void save_WithMinimumMembers_ShouldCreateValidTeam() {
        // Given: 最小2人のメンバーでチームを作成（Teamクラスの制約を考慮）
        Member member1 = memberRepository.findById(new MemberId("550e8400-e29b-41d4-a716-446655440001")).orElseThrow();
        Member member2 = memberRepository.findById(new MemberId("550e8400-e29b-41d4-a716-446655440002")).orElseThrow();
        
        Team newTeam = new Team(new TeamName("最小メンバーチーム"), List.of(member1, member2));

        // When: 保存
        Team savedTeam = teamRepository.save(newTeam);

        // Then: 2人のメンバーでチームが保存されること
        assertThat(savedTeam.getMembers()).hasSize(2);

        // And: データベースから取得しても2人であること
        Optional<Team> retrieved = teamRepository.findById(savedTeam.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getMembers()).hasSize(2);
    }

    @Test
    void save_WithMaximumMembers_ShouldCreateValidTeam() {
        // Given: 最大4人のメンバーでチームを作成（Teamクラスの制約の上限）
        Member member1 = memberRepository.findById(new MemberId("550e8400-e29b-41d4-a716-446655440001")).orElseThrow();
        Member member2 = memberRepository.findById(new MemberId("550e8400-e29b-41d4-a716-446655440002")).orElseThrow();
        Member member3 = memberRepository.findById(new MemberId("550e8400-e29b-41d4-a716-446655440003")).orElseThrow();
        Member member4 = memberRepository.findById(new MemberId("550e8400-e29b-41d4-a716-446655440004")).orElseThrow();
        
        Team newTeam = new Team(new TeamName("最大メンバーチーム"), List.of(member1, member2, member3, member4));

        // When: 保存
        Team savedTeam = teamRepository.save(newTeam);

        // Then: 4人のメンバーでチームが保存されること
        assertThat(savedTeam.getMembers()).hasSize(4);

        // And: データベースから取得しても4人であること
        Optional<Team> retrieved = teamRepository.findById(savedTeam.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getMembers()).hasSize(4);
    }
}