package com.ddd.praha.application.service;

import com.ddd.praha.domain.MemberSearchResult;
import com.ddd.praha.application.repository.MemberTaskRepository;
import com.ddd.praha.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberTaskServiceExtensionTest {

    @Mock
    private MemberTaskRepository memberTaskRepository;

    private MemberTaskService memberTaskService;

    @BeforeEach
    void setUp() {
        memberTaskService = new MemberTaskService(memberTaskRepository);
    }

    @Test
    void findMembersByTasksAndStatuses_shouldReturnPagedMembers() {
        // Given
        List<TaskId> taskIds = Arrays.asList(new TaskId("task1"), new TaskId("task2"));
        List<TaskStatus> statuses = List.of(TaskStatus.レビュー待ち);
        
        Member member1 = new Member(new MemberId("1"), new MemberName("田中太郎"), new Email("tanaka@example.com"), EnrollmentStatus.在籍中);
        Member member2 = new Member(new MemberId("2"), new MemberName("佐藤花子"), new Email("sato@example.com"), EnrollmentStatus.在籍中);
        
        List<Member> expectedMembers = Arrays.asList(member1, member2);
        
        when(memberTaskRepository.findMembersByTasksAndStatuses(taskIds, statuses, 0, 10))
            .thenReturn(expectedMembers);
        when(memberTaskRepository.countMembersByTasksAndStatuses(taskIds, statuses))
            .thenReturn(2L);

        // When
        MemberSearchResult result = memberTaskService.findMembersByTasksAndStatuses(taskIds, statuses, 0, 10);

        // Then
        assertThat(result.getMembers()).hasSize(2);
        assertThat(result.getMembers().get(0).getName().value()).isEqualTo("田中太郎");
        assertThat(result.getMembers().get(1).getName().value()).isEqualTo("佐藤花子");
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(2L);
    }

    @Test
    void findMembersByTasksAndStatuses_shouldReturnEmptyPageWhenNoMatches() {
        // Given
        List<TaskId> taskIds = List.of(new TaskId("nonexistent"));
        List<TaskStatus> statuses = List.of(TaskStatus.レビュー待ち);
        
        when(memberTaskRepository.findMembersByTasksAndStatuses(taskIds, statuses, 0, 10))
            .thenReturn(List.of());
        when(memberTaskRepository.countMembersByTasksAndStatuses(taskIds, statuses))
            .thenReturn(0L);

        // When
        MemberSearchResult result = memberTaskService.findMembersByTasksAndStatuses(taskIds, statuses, 0, 10);

        // Then
        assertThat(result.getMembers()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0L);
    }
}