package com.ddd.praha.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import com.ddd.praha.TestcontainersConfiguration;
import com.ddd.praha.application.repository.MemberRepository;
import com.ddd.praha.domain.Email;
import com.ddd.praha.domain.EnrollmentStatus;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberName;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class MemberRepositoryImplTest {

  @Autowired
  private MemberRepository memberRepository;

  @Test
  void 同じIdで保存されてもエラーが発生しない() {
    MemberId id = new MemberId("test-id");
    MemberName name = new MemberName("name");
    Email email = new Email("initial@example.com");
    Member initialMember = new Member(
        id,
        name,
        email,
        EnrollmentStatus.在籍中
    );

    memberRepository.save(initialMember);
    memberRepository.save(initialMember);
    memberRepository.save(initialMember);

    List<Member> allMembers = memberRepository.getAll();
    assertEquals(1, allMembers.size());

    Member actualMember = allMembers.getFirst();
    assertEquals(id, actualMember.getId());
    assertEquals(name, actualMember.getName());
    assertEquals(email, actualMember.getEmail());
    assertEquals(EnrollmentStatus.在籍中, actualMember.getStatus());

  }

  @Test
  void 参加者の情報を個別に取得できる() {
    MemberId id = new MemberId("test-id");
    MemberName name = new MemberName("name");
    Email email = new Email("initial@example.com");
    Member initialMember = new Member(
        id,
        name,
        email,
        EnrollmentStatus.在籍中
    );
    memberRepository.save(initialMember);
    Member foundMember = memberRepository.get(id);

    assertAll(
        () ->assertEquals("test-id", foundMember.getId().value()),
        () ->assertEquals("name", foundMember.getName().value()),
        () ->assertEquals("initial@example.com", foundMember.getEmail().value()),
        () ->assertEquals(EnrollmentStatus.在籍中, foundMember.getStatus())
    );
  }

  @Test
  void 全参加者の情報を取得できる() {
    MemberId id1 = new MemberId("test-id-1");
    MemberName name1 = new MemberName("name-1");
    Email email1 = new Email("initial-1@example.com");
    Member initialMember1 = new Member(
        id1,
        name1,
        email1,
        EnrollmentStatus.在籍中
    );

    MemberId id2 = new MemberId("test-id-2");
    MemberName name2 = new MemberName("name-2");
    Email email2 = new Email("initial-2@example.com");
    Member initialMember2 = new Member(
        id2,
        name2,
        email2,
        EnrollmentStatus.在籍中
    );


    MemberId id3 = new MemberId("test-id-3");
    MemberName name3 = new MemberName("name-3");
    Email email3 = new Email("initial-3@example.com");
    Member initialMember3 = new Member(
        id3,
        name3,
        email3,
        EnrollmentStatus.在籍中
    );

    memberRepository.save(initialMember1);
    memberRepository.save(initialMember2);

    List<Member> all = memberRepository.getAll();
    assertEquals(3, all.size());
  }

  @Test
  void IDで参加者を検索できる() {
    MemberId id = new MemberId("test-id");
    MemberName name = new MemberName("name");
    Email email = new Email("initial@example.com");
    Member initialMember = new Member(
        id,
        name,
        email,
        EnrollmentStatus.在籍中
    );
    memberRepository.save(initialMember);

    Optional<Member> result = memberRepository.findById(id);
    assertTrue(result.isPresent());
    Member foundMember = result.get();

    assertAll(
        () ->assertEquals("test-id", foundMember.getId().value()),
        () ->assertEquals("name", foundMember.getName().value()),
        () ->assertEquals("initial@example.com", foundMember.getEmail().value()),
        () ->assertEquals(EnrollmentStatus.在籍中, foundMember.getStatus())
    );

  }

  @Test
  void IDで参加者を検索し見つからない場合はEmptyを返す() {
    MemberId id = new MemberId("no-exist-id");
    Optional<Member> result = memberRepository.findById(id);
    assertFalse(result.isPresent());
  }

  @Test
  void 在籍ステータスを更新できる() {
    MemberId id = new MemberId("test-id");
    MemberName name = new MemberName("name");
    Email email = new Email("initial@example.com");
    Member initialMember = new Member(
        id,
        name,
        email,
        EnrollmentStatus.在籍中
    );
    memberRepository.save(initialMember);
    memberRepository.updateStatus(id, EnrollmentStatus.休会中);

    Optional<Member> result = memberRepository.findById(id);
    assertTrue(result.isPresent());
    Member foundMember = result.get();

    assertEquals(EnrollmentStatus.休会中, foundMember.getStatus());
  }
}