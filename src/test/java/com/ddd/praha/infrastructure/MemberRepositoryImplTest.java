package com.ddd.praha.infrastructure;

import com.ddd.praha.TestcontainersConfiguration;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberName;
import com.ddd.praha.domain.Email;
import com.ddd.praha.domain.EnrollmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberRepositoryImplTest {

    @Autowired
    private MemberRepositoryImpl memberRepository;

    @Test
    @DisplayName("全てのメンバーを取得できる")
    void findAll_ReturnsAllMembers() {
        // 準備: ユニークなEmailでテスト用メンバーを作成・保存
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        Member member1 = new Member(new MemberName("テスト太郎" + uniqueId), new Email("test1-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member2 = new Member(new MemberName("テスト次郎" + uniqueId), new Email("test2-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member member3 = new Member(new MemberName("テスト三郎" + uniqueId), new Email("test3-" + uniqueId + "@example.com"), EnrollmentStatus.休会中);
        
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        // 実行
        List<Member> members = memberRepository.findAll();

        // 検証: 保存した3件が取得できること
        assertTrue(members.size() >= 3);
        
        // 保存したメンバーが全て含まれていること
        List<String> memberNames = members.stream()
            .map(member -> member.getName().value())
            .toList();
        assertTrue(memberNames.contains("テスト太郎" + uniqueId));
        assertTrue(memberNames.contains("テスト次郎" + uniqueId));
        assertTrue(memberNames.contains("テスト三郎" + uniqueId));
    }

    @Test
    @DisplayName("存在するIDでメンバーを検索できる")
    void findById_WhenMemberExists_ReturnsMember() {
        // 準備: ユニークなEmailでテスト用メンバーを作成・保存
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        Member testMember = new Member(new MemberName("テスト太郎" + uniqueId), new Email("test-" + uniqueId + "@example.com"), EnrollmentStatus.在籍中);
        Member savedMember = memberRepository.save(testMember);
        MemberId existingId = savedMember.getId();

        // 実行
        Optional<Member> result = memberRepository.findById(existingId);

        // 検証
        assertTrue(result.isPresent());
        assertEquals(existingId, result.get().getId());
        assertEquals("テスト太郎" + uniqueId, result.get().getName().value());
        assertEquals("test-" + uniqueId + "@example.com", result.get().getEmail().value());
    }

    @Test
    @DisplayName("存在しないIDでメンバーを検索するとEmptyが返る")
    void findById_WhenMemberDoesNotExist_ReturnsEmpty() {
        // 準備
        MemberId nonExistentId = new MemberId(UUID.randomUUID().toString());

        // 実行
        Optional<Member> result = memberRepository.findById(nonExistentId);

        // 検証
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("新しいメンバーを保存できる")
    void save_NewMember_InsertsAndReturnsNewMember() {
        // 準備: ユニークなEmailでメンバーを作成
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        MemberName name = new MemberName("新規テスト太郎" + uniqueId);
        Email email = new Email("newtest-" + uniqueId + "@example.com");
        EnrollmentStatus status = EnrollmentStatus.在籍中;
        Member newMember = new Member(name, email, status);

        // 実行
        Member savedMember = memberRepository.save(newMember);

        // 検証
        assertNotNull(savedMember);
        assertEquals(newMember.getId(), savedMember.getId());
        assertEquals(name.value(), savedMember.getName().value());
        assertEquals(email.value(), savedMember.getEmail().value());
        assertEquals(status, savedMember.getStatus());
        
        // データベースから取得して検証
        Optional<Member> retrievedMember = memberRepository.findById(newMember.getId());
        assertTrue(retrievedMember.isPresent());
        assertEquals(name.value(), retrievedMember.get().getName().value());
        assertEquals(email.value(), retrievedMember.get().getEmail().value());
        assertEquals(status, retrievedMember.get().getStatus());
    }

    @Test
    @DisplayName("既存のメンバーを更新できる")
    void save_ExistingMember_UpdatesAndReturnsUpdatedMember() {
        // 準備: ユニークなEmailで新しいメンバーを作成して保存
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        MemberName originalName = new MemberName("更新前太郎" + uniqueId);
        Email originalEmail = new Email("before-" + uniqueId + "@example.com");
        EnrollmentStatus originalStatus = EnrollmentStatus.在籍中;
        Member originalMember = new Member(originalName, originalEmail, originalStatus);
        Member savedOriginal = memberRepository.save(originalMember);

        // メンバーを更新
        MemberName updatedName = new MemberName("更新後太郎" + uniqueId);
        Email updatedEmail = new Email("after-" + uniqueId + "@example.com");
        EnrollmentStatus updatedStatus = EnrollmentStatus.休会中;
        Member updatedMember = new Member(savedOriginal.getId(), updatedName, updatedEmail, updatedStatus);

        // 実行
        Member savedMember = memberRepository.save(updatedMember);

        // 検証
        assertNotNull(savedMember);
        assertEquals(savedOriginal.getId(), savedMember.getId());
        assertEquals(updatedName.value(), savedMember.getName().value());
        assertEquals(updatedEmail.value(), savedMember.getEmail().value());
        assertEquals(updatedStatus, savedMember.getStatus());
        
        // データベースから取得して検証
        Optional<Member> retrievedMember = memberRepository.findById(savedOriginal.getId());
        assertTrue(retrievedMember.isPresent());
        assertEquals(updatedName.value(), retrievedMember.get().getName().value());
        assertEquals(updatedEmail.value(), retrievedMember.get().getEmail().value());
        assertEquals(updatedStatus, retrievedMember.get().getStatus());
    }
}