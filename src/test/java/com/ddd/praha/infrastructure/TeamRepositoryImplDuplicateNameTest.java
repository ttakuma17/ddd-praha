package com.ddd.praha.infrastructure;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.Team;
import com.ddd.praha.domain.model.Email;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberName;
import com.ddd.praha.domain.model.TeamName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class TeamRepositoryImplDuplicateNameTest {

    @Mock
    private TeamMapper teamMapper;

    private TeamRepositoryImpl teamRepository;

    private Team testTeam;

    @BeforeEach
    void setUp() {
        teamRepository = new TeamRepositoryImpl(teamMapper);

        // 最低2名のメンバーを作成（Teamのバリデーション要件）
        Member member1 = new Member(
            new MemberName("Member1"),
            new Email("member1@example.com"),
            EnrollmentStatus.在籍中
        );
        Member member2 = new Member(
            new MemberName("Member2"),
            new Email("member2@example.com"),
            EnrollmentStatus.在籍中
        );

        testTeam = new Team(
            new TeamName("TestTeam"),
            List.of(member1, member2)
        );
    }

    @Test
    void create_正常系_新しいチーム名で作成成功() {
        // Given
        when(teamMapper.exists(testTeam.getId())).thenReturn(false);
        when(teamMapper.findByName("TestTeam")).thenReturn(null);

        // When
        assertDoesNotThrow(() -> teamRepository.create(testTeam));

        // Then
        verify(teamMapper).insert(testTeam);
        verify(teamMapper).addMembers(any(), any());
    }

    @Test
    void create_異常系_チーム名重複で例外() {
        // Given
        when(teamMapper.exists(testTeam.getId())).thenReturn(false);
        TeamRecord existingRecord = new TeamRecord(
            "existing-team-id",
            "TestTeam"
        );
        when(teamMapper.findByName("TestTeam")).thenReturn(existingRecord);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> teamRepository.create(testTeam)
        );

        assertEquals("このチーム名は既に使用されています", exception.getMessage());
        verify(teamMapper, never()).insert(any());
    }

    @Test
    void create_正常系_同じチームIDの更新は許可() {
        // Given - 同じIDのチームが既に存在
        when(teamMapper.exists(testTeam.getId())).thenReturn(false);
        TeamRecord existingRecord = new TeamRecord(
            testTeam.getId().value(),
            "TestTeam"
        );
        when(teamMapper.findByName("TestTeam")).thenReturn(existingRecord);

        // When
        assertDoesNotThrow(() -> teamRepository.create(testTeam));

        // Then
        verify(teamMapper).insert(testTeam);
        verify(teamMapper).addMembers(any(), any());
    }

    @Test
    void create_正常系_異なるチーム名は許可() {
        // Given
        when(teamMapper.exists(testTeam.getId())).thenReturn(false);
        when(teamMapper.findByName("TestTeam")).thenReturn(null);

        // When
        assertDoesNotThrow(() -> teamRepository.create(testTeam));

        // Then
        verify(teamMapper).insert(testTeam);
        verify(teamMapper).addMembers(any(), any());
    }
}