package com.ddd.praha.infrastructure;

import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberName;
import com.ddd.praha.domain.Email;
import com.ddd.praha.domain.EnrollmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MemberRepositoryImpl.class)
class MemberRepositoryImplTest {

    @Autowired
    private MemberRepositoryImpl memberRepository;

    @Test
    @DisplayName("全てのメンバーを取得できる")
    @Sql("/sql/insert_test_members.sql")
    void findAll_ReturnsAllMembers() {
        // 実行
        List<Member> members = memberRepository.findAll();

        // 検証
        assertThat(members).isNotEmpty();
        // テストデータの件数に応じて検証
        assertThat(members.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("存在するIDでメンバーを検索できる")
    @Sql("/sql/insert_test_members.sql")
    void findById_WhenMemberExists_ReturnsMember() {
        // 準備
        // テストデータのIDを取得（実際のテストデータに合わせて調整）
        List<Member> members = memberRepository.findAll();
        MemberId existingId = members.get(0).getId();

        // 実行
        Optional<Member> result = memberRepository.findById(existingId);

        // 検証
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(existingId);
    }

    @Test
    @DisplayName("存在しないIDでメンバーを検索するとEmptyが返る")
    void findById_WhenMemberDoesNotExist_ReturnsEmpty() {
        // 準備
        MemberId nonExistentId = new MemberId(UUID.randomUUID().toString());

        // 実行
        Optional<Member> result = memberRepository.findById(nonExistentId);

        // 検証
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("新しいメンバーを保存できる")
    void save_NewMember_InsertsAndReturnsNewMember() {
        // 準備
        MemberName name = new MemberName("テスト太郎");
        Email email = new Email("test@example.com");
        EnrollmentStatus status = EnrollmentStatus.在籍中;
        Member newMember = new Member(name, email, status);

        // 実行
        Member savedMember = memberRepository.save(newMember);

        // 検証
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getId()).isEqualTo(newMember.getId());
        
        // データベースから取得して検証
        Optional<Member> retrievedMember = memberRepository.findById(newMember.getId());
        assertThat(retrievedMember).isPresent();
        assertThat(retrievedMember.get().getName().value()).isEqualTo(name.value());
        assertThat(retrievedMember.get().getEmail().value()).isEqualTo(email.value());
        assertThat(retrievedMember.get().getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("既存のメンバーを更新できる")
    void save_ExistingMember_UpdatesAndReturnsUpdatedMember() {
        // 準備
        // 新しいメンバーを作成して保存
        MemberName originalName = new MemberName("更新前太郎");
        Email originalEmail = new Email("before@example.com");
        EnrollmentStatus originalStatus = EnrollmentStatus.在籍中;
        Member originalMember = new Member(originalName, originalEmail, originalStatus);
        memberRepository.save(originalMember);

        // メンバーを更新
        MemberName updatedName = new MemberName("更新後太郎");
        Email updatedEmail = new Email("after@example.com");
        EnrollmentStatus updatedStatus = EnrollmentStatus.休会中;
        Member updatedMember = new Member(originalMember.getId(), updatedName, updatedEmail, updatedStatus);

        // 実行
        Member savedMember = memberRepository.save(updatedMember);

        // 検証
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getId()).isEqualTo(originalMember.getId());
        
        // データベースから取得して検証
        Optional<Member> retrievedMember = memberRepository.findById(originalMember.getId());
        assertThat(retrievedMember).isPresent();
        assertThat(retrievedMember.get().getName().value()).isEqualTo(updatedName.value());
        assertThat(retrievedMember.get().getEmail().value()).isEqualTo(updatedEmail.value());
        assertThat(retrievedMember.get().getStatus()).isEqualTo(updatedStatus);
    }
}