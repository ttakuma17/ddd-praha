package com.ddd.praha.infrastructure;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.model.Email;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberRepositoryImplEmailDuplicateTest {

    @Mock
    private MemberMapper memberMapper;

    private MemberRepositoryImpl memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        memberRepository = new MemberRepositoryImpl(memberMapper);

        testMember = new Member(
            new MemberName("テストユーザー"),
            new Email("test@example.com"),
            EnrollmentStatus.在籍中
        );
    }

    @Test
    void save_正常系_新しいメールアドレスで保存成功() {
        // Given
        when(memberMapper.findByEmail("test@example.com")).thenReturn(null);

        // When
        assertDoesNotThrow(() -> memberRepository.save(testMember));

        // Then
        verify(memberMapper).insert(testMember);
    }

    @Test
    void save_異常系_メールアドレス重複で例外() {
        // Given
        MemberRecord existingRecord = new MemberRecord(
            "existing-id",
            "既存ユーザー",
            "test@example.com",
            "在籍中"
        );
        when(memberMapper.findByEmail("test@example.com")).thenReturn(existingRecord);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> memberRepository.save(testMember)
        );

        assertEquals("このメールアドレスは既に使用されています", exception.getMessage());
        verify(memberMapper, never()).insert(any());
    }

    @Test
    void save_正常系_同じメンバーの更新は許可() {
        // Given - 同じIDのメンバーが既に存在
        MemberRecord existingRecord = new MemberRecord(
            testMember.getId().value(),
            "既存ユーザー",
            "test@example.com",
            "在籍中"
        );
        when(memberMapper.findByEmail("test@example.com")).thenReturn(existingRecord);

        // When
        assertDoesNotThrow(() -> memberRepository.save(testMember));

        // Then
        verify(memberMapper).insert(testMember);
    }
}