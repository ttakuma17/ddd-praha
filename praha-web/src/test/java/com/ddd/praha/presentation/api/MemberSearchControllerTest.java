package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.usecase.MemberService;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberSearchController.class)
public class MemberSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    private List<Member> testMembers;
    private List<TaskStatus> testStatuses;

    @BeforeEach
    void setUp() {
        // テスト用のメンバーを作成
        Member member1 = new Member(
            new MemberName("テスト太郎"),
            new Email("taro@example.com"),
            EnrollmentStatus.在籍中
        );
        Member member2 = new Member(
            new MemberName("テスト花子"),
            new Email("hanako@example.com"),
            EnrollmentStatus.在籍中
        );
        testMembers = Arrays.asList(member1, member2);
        
        // テスト用のステータス
        testStatuses = Arrays.asList(TaskStatus.レビュー待ち);
    }

    @Test
    void searchMembersByTasksAndStatuses_正常系_単一課題単一ステータス() throws Exception {
        // Given
        MemberSearchResult searchResult = new MemberSearchResult(testMembers, 0, 10, 2);
        when(memberService.searchMembersByTaskNamesAndStatuses(any(), any(), eq(0), eq(10)))
            .thenReturn(searchResult);

        // When & Then
        String requestJson = """
            {
                "taskNames": ["設計原則（SOLID）"],
                "statuses": ["レビュー待ち"],
                "page": 0
            }
            """;

        mockMvc.perform(post("/api/search/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.members").isArray())
            .andExpect(jsonPath("$.members.length()").value(2))
            .andExpect(jsonPath("$.members[0].name").value("テスト太郎"))
            .andExpect(jsonPath("$.members[1].name").value("テスト花子"))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(10))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.isFirst").value(true))
            .andExpect(jsonPath("$.isLast").value(true));
    }

    @Test
    void searchMembersByTasksAndStatuses_正常系_複数課題複数ステータス() throws Exception {
        // Given
        MemberSearchResult searchResult = new MemberSearchResult(testMembers, 0, 10, 15);
        when(memberService.searchMembersByTaskNamesAndStatuses(any(), any(), eq(0), eq(10)))
            .thenReturn(searchResult);

        // When & Then
        String requestJson = """
            {
                "taskNames": ["設計原則（SOLID）", "DBモデリング1"],
                "statuses": ["未着手", "レビュー待ち"],
                "page": 0
            }
            """;

        mockMvc.perform(post("/api/search/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(15))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.isFirst").value(true))
            .andExpect(jsonPath("$.isLast").value(false));
    }

    @Test
    void searchMembersByTasksAndStatuses_正常系_ページング2ページ目() throws Exception {
        // Given
        MemberSearchResult searchResult = new MemberSearchResult(testMembers, 1, 10, 15);
        when(memberService.searchMembersByTaskNamesAndStatuses(any(), any(), eq(1), eq(10)))
            .thenReturn(searchResult);

        // When & Then
        String requestJson = """
            {
                "taskNames": ["DBモデリング3"],
                "statuses": ["レビュー待ち"],
                "page": 1
            }
            """;

        mockMvc.perform(post("/api/search/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.isFirst").value(false))
            .andExpect(jsonPath("$.isLast").value(true));
    }

    @Test
    void searchMembersByTasksAndStatuses_正常系_空の結果() throws Exception {
        // Given
        MemberSearchResult emptyResult = new MemberSearchResult(List.of(), 0, 10, 0);
        when(memberService.searchMembersByTaskNamesAndStatuses(any(), any(), eq(0), eq(10)))
            .thenReturn(emptyResult);

        // When & Then
        String requestJson = """
            {
                "taskNames": ["存在しない課題"],
                "statuses": ["完了"],
                "page": 0
            }
            """;

        mockMvc.perform(post("/api/search/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.members").isArray())
            .andExpect(jsonPath("$.members.length()").value(0))
            .andExpect(jsonPath("$.totalElements").value(0))
            .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    void searchMembersByTasksAndStatuses_異常系_taskNamesが空() throws Exception {
        // When & Then
        String requestJson = """
            {
                "taskNames": [],
                "statuses": ["レビュー待ち"],
                "page": 0
            }
            """;

        mockMvc.perform(post("/api/search/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void searchMembersByTasksAndStatuses_異常系_statusesが空() throws Exception {
        // When & Then
        String requestJson = """
            {
                "taskNames": ["設計原則（SOLID）"],
                "statuses": [],
                "page": 0
            }
            """;

        mockMvc.perform(post("/api/search/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void searchMembersByTasksAndStatuses_異常系_pageが負の値() throws Exception {
        // When & Then
        String requestJson = """
            {
                "taskNames": ["設計原則（SOLID）"],
                "statuses": ["レビュー待ち"],
                "page": -1
            }
            """;

        mockMvc.perform(post("/api/search/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());
    }
}