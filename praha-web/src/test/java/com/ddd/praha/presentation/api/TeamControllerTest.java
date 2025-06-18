package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.usecase.MemberService;
import com.ddd.praha.application.service.usecase.TeamQueryService;
import com.ddd.praha.application.service.usecase.TeamOrchestrationService;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.Team;
import com.ddd.praha.domain.model.Email;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberId;
import com.ddd.praha.domain.model.MemberName;
import com.ddd.praha.domain.model.TeamId;
import com.ddd.praha.domain.model.TeamName;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeamController.class)
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamQueryService teamQueryService;

    @MockitoBean
    private TeamOrchestrationService teamOrchestrationService;

    @MockitoBean
    private MemberService memberService;

    private Team team1;
    private Team team2;
    private Member member1;
    private Member member2;
    private Member member3;

    @BeforeEach
    public void setUp() {
        // Create test members
        member1 = new Member(
                new MemberName("テスト太郎"),
                new Email("taro@example.com"),
                EnrollmentStatus.在籍中
        );

        member2 = new Member(
                new MemberName("テスト次郎"),
                new Email("jiro@example.com"),
                EnrollmentStatus.在籍中
        );

        member3 = new Member(
                new MemberName("テスト三郎"),
                new Email("saburo@example.com"),
                EnrollmentStatus.在籍中
        );

        // Create test teams
        List<Member> team1Members = new ArrayList<>();
        team1Members.add(member1);
        team1Members.add(member2);
        team1 = new Team(new TeamName("TeamA"), team1Members);

        List<Member> team2Members = new ArrayList<>();
        team2Members.add(member2);
        team2Members.add(member3);
        team2 = new Team(new TeamName("TeamB"), team2Members);

    }

    @Test
    public void 全チームの一覧を取得して返す() throws Exception {
        // Arrange
        List<Team> teams = Arrays.asList(team1, team2);
        when(teamQueryService.getAll()).thenReturn(teams);

        // Act & Assert
        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(team1.getId().value()))
                .andExpect(jsonPath("$[0].name").value("TeamA"))
                .andExpect(jsonPath("$[0].members[0].id").value(member1.getId().value()))
                .andExpect(jsonPath("$[0].members[1].id").value(member2.getId().value()))
                .andExpect(jsonPath("$[1].id").value(team2.getId().value()))
                .andExpect(jsonPath("$[1].name").value("TeamB"))
                .andExpect(jsonPath("$[1].members[0].id").value(member2.getId().value()))
                .andExpect(jsonPath("$[1].members[1].id").value(member3.getId().value()));
    }

    @Test
    public void チームが存在する場合にIDでチームを取得して返す() throws Exception {
        // Arrange
        when(teamQueryService.get(new TeamId("team-1"))).thenReturn(team1);

        // Act & Assert
        mockMvc.perform(get("/api/teams/team-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(team1.getId().value()))
                .andExpect(jsonPath("$.name").value("TeamA"))
                .andExpect(jsonPath("$.members[0].id").value(member1.getId().value()))
                .andExpect(jsonPath("$.members[1].id").value(member2.getId().value()));
    }

    @Test
    public void チームが存在しない場合にNotFoundを返す() throws Exception {
        // Arrange
        when(teamQueryService.get(new TeamId("non-existent"))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/teams/non-existent"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void チームとメンバーが存在する場合にチームメンバーを更新して更新されたチームを返す() throws Exception {
        // Arrange
        TeamMemberUpdateRequest request = new TeamMemberUpdateRequest(
                Arrays.asList(member1.getId().value(), member3.getId().value())
        );

        when(teamQueryService.get(new TeamId("team-1"))).thenReturn(team1);
        when(memberService.get(member1.getId())).thenReturn(member1);
        when(memberService.get(member3.getId())).thenReturn(member3);

        // Mock the team after removing member2
        Team updatedTeam1 = new Team(new TeamName("TeamA"), Arrays.asList(member1, member3)) {
            @Override
            public TeamId getId() {
                return new TeamId("team-1");
            }
        };

        when(teamOrchestrationService.removeMemberFromTeam(team1.getId(), member2)).thenReturn(team1);
        when(teamOrchestrationService.addMemberToTeam(team1.getId(), member3)).thenReturn(updatedTeam1);

        // Act & Assert
        String requestJson = """
                {
                    "memberIds": ["%s", "%s"]
                }
                """.formatted(member1.getId().value(), member3.getId().value());

        mockMvc.perform(put("/api/teams/team-1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedTeam1.getId().value()))
                .andExpect(jsonPath("$.name").value("TeamA"))
                .andExpect(jsonPath("$.members[0].id").value(member1.getId().value()))
                .andExpect(jsonPath("$.members[1].id").value(member3.getId().value()));
    }

    @Test
    public void メンバー更新時にチームが存在しない場合にNotFoundを返す() throws Exception {
        // Arrange
        TeamMemberUpdateRequest request = new TeamMemberUpdateRequest(
                Arrays.asList(member1.getId().value(), member3.getId().value())
        );

        when(teamQueryService.get(new TeamId("non-existent"))).thenReturn(null);

        // Act & Assert
        String requestJson = """
                {
                    "memberIds": ["%s", "%s"]
                }
                """.formatted(member1.getId().value(), member3.getId().value());

        mockMvc.perform(put("/api/teams/non-existent/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void メンバーが存在しない場合にBadRequestを返す() throws Exception {
        // Arrange
        TeamMemberUpdateRequest request = new TeamMemberUpdateRequest(
                Arrays.asList(member1.getId().value(), "non-existent-member")
        );

        when(teamQueryService.get(new TeamId("team-1"))).thenReturn(team1);
        when(memberService.get(member1.getId())).thenReturn(member1);
        when(memberService.get(new MemberId("non-existent-member"))).thenThrow(new RuntimeException("Member not found"));

        // Act & Assert
        String requestJson = """
                {
                    "memberIds": ["%s", "%s"]
                }
                """.formatted(member1.getId().value(), "non-existent-member");

        mockMvc.perform(put("/api/teams/team-1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 不正な引数例外の場合にBadRequestを返す() throws Exception {
        // Arrange
        TeamMemberUpdateRequest request = new TeamMemberUpdateRequest(
                Arrays.asList(member1.getId().value())
        );

        when(teamQueryService.get(new TeamId("team-1"))).thenReturn(team1);
        when(memberService.get(member1.getId())).thenReturn(member1);

        // Use doThrow instead of when().thenThrow()
        Mockito.doThrow(new IllegalArgumentException("Error"))
               .when(teamOrchestrationService).removeMemberFromTeam(any(), any());

        // Act & Assert
        String requestJson = """
                {
                    "memberIds": ["%s"]
                }
                """.formatted(member1.getId().value());

        mockMvc.perform(put("/api/teams/team-1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 不正な状態例外の場合にConflictを返す() throws Exception {
        // Arrange
        TeamMemberUpdateRequest request = new TeamMemberUpdateRequest(
            Collections.singletonList(member1.getId().value())
        );

        when(teamQueryService.get(new TeamId("team-1"))).thenReturn(team1);
        when(memberService.get(member1.getId())).thenReturn(member1);
        when(teamOrchestrationService.removeMemberFromTeam(any(), any())).thenThrow(new IllegalStateException("Error"));

        // Act & Assert
        String requestJson = """
                {
                    "memberIds": ["%s"]
                }
                """.formatted(member1.getId().value());

        mockMvc.perform(put("/api/teams/team-1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isConflict());
    }
}
