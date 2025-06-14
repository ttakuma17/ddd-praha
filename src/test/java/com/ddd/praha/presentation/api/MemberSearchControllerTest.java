package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.usecase.MemberTaskService;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.model.Email;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.model.MemberName;
import com.ddd.praha.domain.model.MemberSearchResult;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberSearchController.class)
class MemberSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberTaskService memberTaskService;

    @Test
    void searchMembersByTasksAndStatuses_shouldReturnPagedResult() throws Exception {
        // Given
        Member member1 = new Member(new MemberId("1"), new MemberName("田中太郎"), new Email("tanaka@example.com"), EnrollmentStatus.在籍中);
        Member member2 = new Member(new MemberId("2"), new MemberName("佐藤花子"), new Email("sato@example.com"), EnrollmentStatus.在籍中);
        
        MemberSearchResult searchResult = new MemberSearchResult(
            Arrays.asList(member1, member2), 
            0, 
            10, 
            2L
        );

        when(memberTaskService.findMembersByTasksAndStatuses(
            anyList(), anyList(), eq(0), eq(10)
        )).thenReturn(searchResult);

        // When & Then
        String requestBody = """
            {
                "taskIds": ["task1", "task2"],
                "statuses": ["レビュー待ち"],
                "page": 0
            }
            """;
        
        mockMvc.perform(post("/api/members/search-by-tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("田中太郎"))
                .andExpect(jsonPath("$.content[1].name").value("佐藤花子"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void searchMembersByTasksAndStatuses_shouldReturnEmptyResult() throws Exception {
        // Given
        MemberSearchResult emptyResult = new MemberSearchResult(
            List.of(),
            0, 
            10, 
            0L
        );

        when(memberTaskService.findMembersByTasksAndStatuses(
            anyList(), anyList(), eq(0), eq(10)
        )).thenReturn(emptyResult);

        // When & Then
        String requestBody = """
            {
                "taskIds": ["nonexistent"],
                "statuses": ["レビュー待ち"],
                "page": 0
            }
            """;
        
        mockMvc.perform(post("/api/members/search-by-tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}