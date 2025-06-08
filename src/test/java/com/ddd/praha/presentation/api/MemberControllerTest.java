package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.MemberService;
import com.ddd.praha.domain.Email;
import com.ddd.praha.domain.EnrollmentStatus;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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

        // テスト用のMemberオブジェクトを作成するためのモックを設定
        testMember = new Member(name, email, status) {
            @Override
            public MemberId getId() {
                return testMemberId;
            }
        };
    }

    @Test
    void getAllMembers_ReturnsListOfMembers() throws Exception {
        // モックの設定
        List<Member> members = Arrays.asList(testMember);
        when(memberService.getAllMembers()).thenReturn(members);

        // APIリクエストの実行と検証
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testMember.getId().value()))
                .andExpect(jsonPath("$[0].name").value(testMember.getName().value()))
                .andExpect(jsonPath("$[0].email").value(testMember.getEmail().value()))
                .andExpect(jsonPath("$[0].status").value(testMember.getStatus().name()));
    }

    @Test
    void getMemberById_WhenMemberExists_ReturnsMember() throws Exception {
        // モックの設定
        when(memberService.getMemberById(any(MemberId.class))).thenReturn(Optional.of(testMember));

        // APIリクエストの実行と検証
        mockMvc.perform(get("/api/members/{id}", testMemberId.value()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testMember.getId().value()))
                .andExpect(jsonPath("$.name").value(testMember.getName().value()))
                .andExpect(jsonPath("$.email").value(testMember.getEmail().value()))
                .andExpect(jsonPath("$.status").value(testMember.getStatus().name()));
    }

    @Test
    void getMemberById_WhenMemberDoesNotExist_ReturnsNotFound() throws Exception {
        // モックの設定
        when(memberService.getMemberById(any(MemberId.class))).thenReturn(Optional.empty());

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

        // モックの設定
        when(memberService.addMember(
                any(MemberName.class),
                any(Email.class),
                any(EnrollmentStatus.class)
        )).thenReturn(testMember);

        // APIリクエストの実行と検証
        String requestJson = """
                {
                    "name": "%s",
                    "email": "%s",
                    "status": "%s"
                }
                """.formatted(
                request.getName(),
                request.getEmail(),
                request.getStatus()
        );

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testMember.getId().value()))
                .andExpect(jsonPath("$.name").value(testMember.getName().value()))
                .andExpect(jsonPath("$.email").value(testMember.getEmail().value()))
                .andExpect(jsonPath("$.status").value(testMember.getStatus().name()));
    }

    @Test
    void updateMemberStatus_WhenMemberExists_ReturnsUpdatedMember() throws Exception {
        // リクエストの作成
        MemberStatusUpdateRequest request = new MemberStatusUpdateRequest(EnrollmentStatus.休会中.name());

        // 更新後のメンバー
        Member updatedMember = new Member(
                testMember.getName(),
                testMember.getEmail(),
                EnrollmentStatus.休会中
        ) {
            @Override
            public MemberId getId() {
                return testMemberId;
            }
        };

        // モックの設定
        when(memberService.updateMemberStatus(
                any(MemberId.class),
                any(EnrollmentStatus.class)
        )).thenReturn(updatedMember);

        // APIリクエストの実行と検証
        String requestJson = """
                {
                    "status": "%s"
                }
                """.formatted(request.getStatus());

        mockMvc.perform(put("/api/members/{id}/status", testMemberId.value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedMember.getId().value()))
                .andExpect(jsonPath("$.name").value(updatedMember.getName().value()))
                .andExpect(jsonPath("$.email").value(updatedMember.getEmail().value()))
                .andExpect(jsonPath("$.status").value(updatedMember.getStatus().name()));
    }

    @Test
    void updateMemberStatus_WhenMemberDoesNotExist_ReturnsNotFound() throws Exception {
        // リクエストの作成
        MemberStatusUpdateRequest request = new MemberStatusUpdateRequest(EnrollmentStatus.休会中.name());

        // モックの設定
        when(memberService.updateMemberStatus(
                any(MemberId.class),
                any(EnrollmentStatus.class)
        )).thenThrow(new IllegalArgumentException("指定されたIDの参加者が見つかりません"));

        // APIリクエストの実行と検証
        String requestJson = """
                {
                    "status": "%s"
                }
                """.formatted(request.getStatus());

        mockMvc.perform(put("/api/members/{id}/status", "non-existent-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMemberStatus_WhenStatusTransitionIsInvalid_ReturnsBadRequest() throws Exception {
        // リクエストの作成
        MemberStatusUpdateRequest request = new MemberStatusUpdateRequest(EnrollmentStatus.退会済.name());

        // モックの設定
        when(memberService.updateMemberStatus(
                any(MemberId.class),
                any(EnrollmentStatus.class)
        )).thenThrow(new IllegalStateException("このステータス変更は許可されていません"));

        // APIリクエストの実行と検証
        String requestJson = """
                {
                    "status": "%s"
                }
                """.formatted(request.getStatus());

        mockMvc.perform(put("/api/members/{id}/status", testMemberId.value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}
