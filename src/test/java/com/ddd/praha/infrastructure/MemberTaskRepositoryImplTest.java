package com.ddd.praha.infrastructure;

import com.ddd.praha.application.repository.TaskRepository;
import com.ddd.praha.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberTaskRepositoryImplExtensionTest {

    @Mock
    private MemberTaskMapper memberTaskMapper;

    @Mock 
    private TaskRepository taskRepository;

    private MemberTaskRepositoryImpl memberTaskRepository;

    @BeforeEach
    void setUp() {
        memberTaskRepository = new MemberTaskRepositoryImpl(memberTaskMapper, taskRepository);
    }

    @Test
    void findMembersByTasksAndStatuses_shouldReturnMatchingMembers() {
        // Given
        List<TaskId> taskIds = Arrays.asList(new TaskId("task1"), new TaskId("task2"));
        List<TaskStatus> statuses = Arrays.asList(TaskStatus.レビュー待ち);
        
        Member member1 = new Member(new MemberId("1"), new MemberName("田中太郎"), new Email("tanaka@example.com"), EnrollmentStatus.在籍中);
        Member member2 = new Member(new MemberId("2"), new MemberName("佐藤花子"), new Email("sato@example.com"), EnrollmentStatus.在籍中);
        
        List<String> taskIdStrings = Arrays.asList("task1", "task2");
        List<String> statusStrings = Arrays.asList("レビュー待ち");
        
        when(memberTaskMapper.findMembersByTasksAndStatuses(taskIdStrings, statusStrings, 0, 10))
            .thenReturn(Arrays.asList(member1, member2));

        // When
        List<Member> result = memberTaskRepository.findMembersByTasksAndStatuses(taskIds, statuses, 0, 10);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName().value()).isEqualTo("田中太郎");
        assertThat(result.get(1).getName().value()).isEqualTo("佐藤花子");
    }

    @Test
    void countMembersByTasksAndStatuses_shouldReturnCorrectCount() {
        // Given
        List<TaskId> taskIds = Arrays.asList(new TaskId("task1"));
        List<TaskStatus> statuses = Arrays.asList(TaskStatus.未着手);
        
        List<String> taskIdStrings = Arrays.asList("task1");
        List<String> statusStrings = Arrays.asList("未着手");
        
        when(memberTaskMapper.countMembersByTasksAndStatuses(taskIdStrings, statusStrings))
            .thenReturn(5L);

        // When
        long result = memberTaskRepository.countMembersByTasksAndStatuses(taskIds, statuses);

        // Then
        assertThat(result).isEqualTo(5L);
    }
}