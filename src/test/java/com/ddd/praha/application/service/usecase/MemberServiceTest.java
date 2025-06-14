package com.ddd.praha.application.service.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.ddd.praha.application.repository.MemberRepository;
import com.ddd.praha.application.repository.TeamRepository;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.Team;
import com.ddd.praha.domain.model.Email;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.model.MemberName;
import com.ddd.praha.domain.model.TeamId;
import com.ddd.praha.domain.model.TeamName;
import com.ddd.praha.domain.model.TaskId;
import com.ddd.praha.domain.model.TaskStatus;
import com.ddd.praha.domain.model.MemberSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Test
    void searchMembersByTasksAndStatuses_指定した条件でメンバーを検索できる() {
        // 準備
        List<TaskId> taskIds = Arrays.asList(new TaskId("task-1"), new TaskId("task-2"));
        List<TaskStatus> statuses = Arrays.asList(TaskStatus.レビュー待ち);
        int page = 0;
        int size = 10;
        
        List<Member> expectedMembers = Arrays.asList(
            new Member(
                new MemberId("member-001"),
                new MemberName("田中太郎"),
                new Email("tanaka@example.com"),
                EnrollmentStatus.在籍中
            ),
            new Member(
                new MemberId("member-002"),
                new MemberName("佐藤花子"),
                new Email("sato@example.com"),
                EnrollmentStatus.在籍中
            )
        );
        
        MemberSearchResult expectedResult = new MemberSearchResult(expectedMembers, page, size, 15);
        when(memberRepository.findMembersByTasksAndStatuses(taskIds, statuses, page, size))
            .thenReturn(expectedMembers);

        // 実行
        MemberSearchResult result = memberService.searchMembersByTasksAndStatuses(taskIds, statuses, page, size);
        
        // 検証
        assertNotNull(result);
        assertEquals(2, result.getMembers().size());
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals(15, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(true, result.isFirst());
        assertEquals(false, result.isLast());
    }

    @Test
    void searchMembersByTasksAndStatuses_結果が空の場合() {
        // 準備
        List<TaskId> taskIds = Arrays.asList(new TaskId("task-1"));
        List<TaskStatus> statuses = Arrays.asList(TaskStatus.完了);
        int page = 0;
        int size = 10;
        
        when(memberRepository.findMembersByTasksAndStatuses(taskIds, statuses, page, size))
            .thenReturn(List.of());

        // 実行
        MemberSearchResult result = memberService.searchMembersByTasksAndStatuses(taskIds, statuses, page, size);
        
        // 検証
        assertNotNull(result);
        assertEquals(0, result.getMembers().size());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    void searchMembersByTasksAndStatuses_2ページ目の検索() {
        // 準備
        List<TaskId> taskIds = Arrays.asList(new TaskId("task-1"));
        List<TaskStatus> statuses = Arrays.asList(TaskStatus.取組中);
        int page = 1;
        int size = 10;
        
        List<Member> expectedMembers = Arrays.asList(
            new Member(
                new MemberId("member-011"),
                new MemberName("鈴木一郎"),
                new Email("suzuki@example.com"),
                EnrollmentStatus.在籍中
            )
        );
        
        when(memberRepository.findMembersByTasksAndStatuses(taskIds, statuses, page, size))
            .thenReturn(expectedMembers);

        // 実行
        MemberSearchResult result = memberService.searchMembersByTasksAndStatuses(taskIds, statuses, page, size);
        
        // 検証
        assertNotNull(result);
        assertEquals(1, result.getMembers().size());
        assertEquals(1, result.getPage());
        assertEquals(11, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(false, result.isFirst());
        assertEquals(true, result.isLast());
    }
}