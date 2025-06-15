package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.usecase.MemberService;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.model.Email;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.model.MemberName;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MemberService memberService;

  private Member testMember;
  private MemberId testMemberId;

  @BeforeEach
  void setUp() {
    // テスト用のメンバーを作成
    testMemberId = new MemberId("test-id-1");
    MemberName name = new MemberName("テスト太郎");
    Email email = new Email("test@example.com");
    EnrollmentStatus status = EnrollmentStatus.在籍中;
    testMember = new Member(testMemberId,name, email, status);
  }

  @Test
  void getAllMembers_ReturnsListOfMembers() throws Exception {
    List<Member> members = Collections.singletonList(testMember);
    when(memberService.getAll()).thenReturn(members);

    mockMvc.perform(get("/api/members"))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            [{
                "id": "test-id-1",
                "name": "テスト太郎",
                "email": "test@example.com",
                "status": "在籍中"
            }]
            """));
  }

  @Test
  void findMemberById_WhenMemberExists_ReturnsMember() throws Exception {
    when(memberService.findById(any(MemberId.class))).thenReturn(Optional.of(testMember));

    mockMvc.perform(get("/api/members/{id}", testMemberId.value()))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
                "id": "test-id-1",
                "name": "テスト太郎",
                "email": "test@example.com",
                "status": "在籍中"
            }
            """));
  }

  @Test
  void findMemberById_WhenMemberDoesNotExist_ReturnsNotFound() throws Exception {
    // モックの設定
    when(memberService.findById(any(MemberId.class))).thenReturn(Optional.empty());

    // APIリクエストの実行と検証
    mockMvc.perform(get("/api/members/{id}", "non-existent-id"))
        .andExpect(status().isNotFound());
  }

  @Test
  void createMember_ReturnsCreatedMember() throws Exception {
    // リクエストの作成
    MemberCreateRequest request = new MemberCreateRequest(
        testMember.getName().value(),
        testMember.getEmail().value(),
        testMember.getStatus().name()
    );

    // APIリクエストの実行と検証
    String requestJson = """
        {
            "name": "%s",
            "email": "%s",
            "status": "%s"
        }
        """.formatted(
        request.name(),
        request.email(),
        request.status()
    );

    mockMvc.perform(post("/api/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isCreated());
  }

  @Test
  void updateMemberStatus_WhenMemberExists_ReturnsUpdatedMember() throws Exception {
    // リクエストの作成
    MemberStatusUpdateRequest request = new MemberStatusUpdateRequest(
        EnrollmentStatus.休会中.name());

    // モックの設定
    doNothing().when(memberService).updateMemberStatus(any(), any());

    // APIリクエストの実行と検証
    String requestJson = """
        {
            "status": "%s"
        }
        """.formatted(request.status());

    mockMvc.perform(put("/api/members/{id}/status", testMemberId.value())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isOk());
  }

  @Test
  void updateMemberStatus_WhenMemberDoesNotExist_ReturnsNotFound() throws Exception {
    // リクエストの作成
    MemberStatusUpdateRequest request = new MemberStatusUpdateRequest(
        EnrollmentStatus.休会中.name());

    // 存在しないメンバーの場合の振る舞いを設定
    doThrow(new com.ddd.praha.presentation.exception.ResourceNotFoundException("Member not found"))
        .when(memberService).updateMemberStatus(any(), any());

    // APIリクエストの実行と検証
    String requestJson = """
        {
            "status": "%s"
        }
        """.formatted(request.status());

    mockMvc.perform(put("/api/members/{id}/status", "non-existent-id")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateMemberStatus_WhenStatusTransitionIsInvalid_ReturnsBadRequest() throws Exception {
    // リクエストの作成
    MemberStatusUpdateRequest request = new MemberStatusUpdateRequest(
        EnrollmentStatus.退会済.name());

    // 不正な状態遷移の場合の振る舞いを設定
    doThrow(new IllegalStateException("Invalid status transition"))
        .when(memberService).updateMemberStatus(any(), any());

    // APIリクエストの実行と検証
    String requestJson = """
        {
            "status": "%s"
        }
        """.formatted(request.status());

    mockMvc.perform(put("/api/members/{id}/status", testMemberId.value())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isConflict());
  }
}
