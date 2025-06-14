package com.ddd.praha.application.service.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.ddd.praha.application.repository.MemberRepository;
import com.ddd.praha.application.repository.TeamRepository;
import com.ddd.praha.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamOrchestrationService teamOrchestrationService;

    private MemberService memberService;
    private Team testTeam;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberRepository, teamRepository, teamOrchestrationService);

      Member testMember = new Member(
          new MemberId("member-001"),
          new MemberName("田中太郎"),
          new Email("tanaka@example.com"),
          EnrollmentStatus.在籍中
      );

        testTeam = new Team(
            new TeamId("team-001"),
            new TeamName("テストチーム"),
            Arrays.asList(
                testMember,
                new Member(
                    new MemberId("member-002"),
                    new MemberName("佐藤花子"),
                    new Email("sato@example.com"),
                    EnrollmentStatus.在籍中
                )
            )
        );
    }

    @Test
    void updateMemberStatus_在籍中に復帰した場合は自動でチームに割り当てられる() {
        // 準備
        Member member = new Member(
            new MemberId("member-001"),
            new MemberName("田中太郎"),
            new Email("tanaka@example.com"),
            EnrollmentStatus.休会中
        );

        when(memberRepository.get(member.getId())).thenReturn(member);

        // 実行
        memberService.updateMemberStatus(member.getId(), EnrollmentStatus.在籍中);

        // 検証
        verify(memberRepository).updateStatus(member.getId(), EnrollmentStatus.在籍中);
        verify(teamOrchestrationService).assignMemberToTeam(any(Member.class));
    }

    @Test
    void updateMemberStatus_休会に変更した場合はチームから削除される() {
        // 準備
        Member member = new Member(
            new MemberId("member-001"),
            new MemberName("田中太郎"),
            new Email("tanaka@example.com"),
            EnrollmentStatus.在籍中
        );

        when(memberRepository.get(member.getId())).thenReturn(member);
        when(teamRepository.getAll()).thenReturn(List.of(testTeam));

        // 実行
        memberService.updateMemberStatus(member.getId(), EnrollmentStatus.休会中);

        // 検証
        verify(memberRepository).updateStatus(member.getId(), EnrollmentStatus.休会中);
        verify(teamOrchestrationService).removeMemberFromTeam(eq(testTeam.getId()), any(Member.class));
    }

    @Test
    void updateMemberStatus_退会に変更した場合はチームから削除される() {
        // 準備
        Member member = new Member(
            new MemberId("member-001"),
            new MemberName("田中太郎"),
            new Email("tanaka@example.com"),
            EnrollmentStatus.在籍中
        );

        when(memberRepository.get(member.getId())).thenReturn(member);
        when(teamRepository.getAll()).thenReturn(List.of(testTeam));

        // 実行
        memberService.updateMemberStatus(member.getId(), EnrollmentStatus.退会済);

        // 検証
        verify(memberRepository).updateStatus(member.getId(), EnrollmentStatus.退会済);
        verify(teamOrchestrationService).removeMemberFromTeam(eq(testTeam.getId()), any(Member.class));
    }

    @Test
    void updateMemberStatus_同じステータスに変更する場合はチーム操作は行わない() {
        // 準備
        Member member = new Member(
            new MemberId("member-001"),
            new MemberName("田中太郎"),
            new Email("tanaka@example.com"),
            EnrollmentStatus.在籍中
        );

        when(memberRepository.get(member.getId())).thenReturn(member);
        assertThrows(IllegalStateException.class,() -> memberService.updateMemberStatus(member.getId(), EnrollmentStatus.在籍中));
    }

    @Test
    void updateMemberStatus_チームに所属していないメンバーが休会する場合はチーム操作は行わない() {
        // 準備
        Member member = new Member(
            new MemberId("member-999"),
            new MemberName("田中太郎"),
            new Email("tanaka@example.com"),
            EnrollmentStatus.在籍中
        );

        when(memberRepository.get(member.getId())).thenReturn(member);
        when(teamRepository.getAll()).thenReturn(List.of(testTeam)); // member-999は含まれていない

        // 実行
        memberService.updateMemberStatus(member.getId(), EnrollmentStatus.休会中);

        // 検証
        verify(memberRepository).updateStatus(member.getId(), EnrollmentStatus.休会中);
        verify(teamOrchestrationService, never()).removeMemberFromTeam(any(), any());
    }

    @Test
    void findMemberTeam_メンバーが所属しているチームを正しく見つける() {
        // 準備
        MemberId memberId = new MemberId("member-001");
        when(teamRepository.getAll()).thenReturn(List.of(testTeam));

        // リフレクションを使ってprivateメソッドをテスト
        // 実際の実装では、メンバーがチームから削除される動作を確認する
        Member member = new Member(
            memberId,
            new MemberName("田中太郎"),
            new Email("tanaka@example.com"),
            EnrollmentStatus.在籍中
        );

        when(memberRepository.get(memberId)).thenReturn(member);

        // 実行
        memberService.updateMemberStatus(memberId, EnrollmentStatus.休会中);

        // 検証 - チームから削除されることを確認
        verify(teamOrchestrationService).removeMemberFromTeam(eq(testTeam.getId()), any(Member.class));
    }

    @Test
    void updateMemberStatus_退会済みから在籍中への復帰() {
        // 準備
        Member member = new Member(
            new MemberId("member-001"),
            new MemberName("田中太郎"),
            new Email("tanaka@example.com"),
            EnrollmentStatus.退会済
        );

        when(memberRepository.get(member.getId())).thenReturn(member);

        // 実行
        memberService.updateMemberStatus(member.getId(), EnrollmentStatus.在籍中);

        // 検証
        verify(memberRepository).updateStatus(member.getId(), EnrollmentStatus.在籍中);
        verify(teamOrchestrationService).assignMemberToTeam(any(Member.class));
    }

    @Test
    void updateMemberStatus_休会中から在籍中への復帰() {
        // 準備
        Member member = new Member(
            new MemberId("member-001"),
            new MemberName("田中太郎"),
            new Email("tanaka@example.com"),
            EnrollmentStatus.休会中
        );

        when(memberRepository.get(member.getId())).thenReturn(member);

        // 実行
        memberService.updateMemberStatus(member.getId(), EnrollmentStatus.在籍中);

        // 検証
        verify(memberRepository).updateStatus(member.getId(), EnrollmentStatus.在籍中);
        verify(teamOrchestrationService).assignMemberToTeam(any(Member.class));
    }
}