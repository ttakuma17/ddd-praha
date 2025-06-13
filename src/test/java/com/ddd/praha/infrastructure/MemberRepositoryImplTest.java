package com.ddd.praha.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import com.ddd.praha.TestcontainersConfiguration;
import com.ddd.praha.application.repository.MemberRepository;
import com.ddd.praha.domain.Email;
import com.ddd.praha.domain.EnrollmentStatus;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberName;
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
  void save() {
  }

  @Test
  void get() {
  }

  @Test
  void getAll() {
  }

  @Test
  void findById() {
  }

  @Test
  void updateStatus() {

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

    Member updatedMember = new Member(
        id,
        name,
        email,
        EnrollmentStatus.休会中
    );

    memberRepository.updateStatus(updatedMember);

    Optional<Member> result = memberRepository.findById(id);
    assertTrue(result.isPresent());
    Member foundMember = result.get();

    assertEquals(EnrollmentStatus.休会中, foundMember.getStatus());
  }
}