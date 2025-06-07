package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.MemberService;
import com.ddd.praha.application.service.TeamService;
import com.ddd.praha.domain.Email;
import com.ddd.praha.domain.EnrollmentStatus;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberName;
import com.ddd.praha.domain.Team;
import com.ddd.praha.domain.TeamId;
import com.ddd.praha.domain.TeamName;
import com.ddd.praha.presentation.request.TeamMemberUpdateRequest;
import com.ddd.praha.presentation.response.TeamResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeamController.class)
public class TeamControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public TeamService teamService() {
            return Mockito.mock(TeamService.class);
        }

        @Bean
        @Primary
        public MemberService memberService() {
            return Mockito.mock(MemberService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamService teamService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

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
        team1 = new Team(new TeamName("TeamA"), team1Members) {
            @Override
            public TeamId getId() {
                return new TeamId("team-1");
            }
        };

        List<Member> team2Members = new ArrayList<>();
        team2Members.add(member2);
        team2Members.add(member3);
        team2 = new Team(new TeamName("TeamB"), team2Members) {
            @Override
            public TeamId getId() {
                return new TeamId("team-2");
            }
        };
    }

    @Test
    public void getAllTeams_ReturnsListOfTeams() throws Exception {
        // Arrange
        List<Team> teams = Arrays.asList(team1, team2);
        when(teamService.getAllTeams()).thenReturn(teams);

        // Act & Assert
        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("team-1"))
                .andExpect(jsonPath("$[0].name").value("TeamA"))
                .andExpect(jsonPath("$[0].members[0].id").value(member1.getId().value()))
                .andExpect(jsonPath("$[0].members[1].id").value(member2.getId().value()))
                .andExpect(jsonPath("$[1].id").value("team-2"))
                .andExpect(jsonPath("$[1].name").value("TeamB"))
                .andExpect(jsonPath("$[1].members[0].id").value(member2.getId().value()))
                .andExpect(jsonPath("$[1].members[1].id").value(member3.getId().value()));
    }

    @Test
    public void getTeamById_WhenTeamExists_ReturnsTeam() throws Exception {
        // Arrange
        when(teamService.getTeamById(new TeamId("team-1"))).thenReturn(Optional.of(team1));

        // Act & Assert
        mockMvc.perform(get("/api/teams/team-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("team-1"))
                .andExpect(jsonPath("$.name").value("TeamA"))
                .andExpect(jsonPath("$.members[0].id").value(member1.getId().value()))
                .andExpect(jsonPath("$.members[1].id").value(member2.getId().value()));
    }

    @Test
    public void getTeamById_WhenTeamDoesNotExist_ReturnsNotFound() throws Exception {
        // Arrange
        when(teamService.getTeamById(new TeamId("non-existent"))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/teams/non-existent"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateTeamMembers_WhenTeamAndMembersExist_ReturnsUpdatedTeam() throws Exception {
        // Arrange
        TeamMemberUpdateRequest request = new TeamMemberUpdateRequest(
                Arrays.asList(member1.getId().value(), member3.getId().value())
        );

        when(teamService.getTeamById(new TeamId("team-1"))).thenReturn(Optional.of(team1));
        when(memberService.getMemberById(member1.getId())).thenReturn(Optional.of(member1));
        when(memberService.getMemberById(member3.getId())).thenReturn(Optional.of(member3));

        // Mock the team after removing member2
        Team updatedTeam1 = new Team(new TeamName("TeamA"), Arrays.asList(member1, member3)) {
            @Override
            public TeamId getId() {
                return new TeamId("team-1");
            }
        };

        when(teamService.removeMemberFromTeam(team1.getId(), member2)).thenReturn(team1);
        when(teamService.addMemberToTeam(team1.getId(), member3)).thenReturn(updatedTeam1);

        // Act & Assert
        mockMvc.perform(put("/api/teams/team-1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("team-1"))
                .andExpect(jsonPath("$.name").value("TeamA"))
                .andExpect(jsonPath("$.members[0].id").value(member1.getId().value()))
                .andExpect(jsonPath("$.members[1].id").value(member3.getId().value()));
    }

    @Test
    public void updateTeamMembers_WhenTeamDoesNotExist_ReturnsNotFound() throws Exception {
        // Arrange
        TeamMemberUpdateRequest request = new TeamMemberUpdateRequest(
                Arrays.asList(member1.getId().value(), member3.getId().value())
        );

        when(teamService.getTeamById(new TeamId("non-existent"))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/teams/non-existent/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateTeamMembers_WhenMemberDoesNotExist_ReturnsBadRequest() throws Exception {
        // Arrange
        TeamMemberUpdateRequest request = new TeamMemberUpdateRequest(
                Arrays.asList(member1.getId().value(), "non-existent-member")
        );

        when(teamService.getTeamById(new TeamId("team-1"))).thenReturn(Optional.of(team1));
        when(memberService.getMemberById(member1.getId())).thenReturn(Optional.of(member1));
        when(memberService.getMemberById(new MemberId("non-existent-member"))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/teams/team-1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateTeamMembers_WhenIllegalArgumentException_ReturnsBadRequest() throws Exception {
        // Arrange
        TeamMemberUpdateRequest request = new TeamMemberUpdateRequest(
                Arrays.asList(member1.getId().value())
        );

        when(teamService.getTeamById(new TeamId("team-1"))).thenReturn(Optional.of(team1));
        when(memberService.getMemberById(member1.getId())).thenReturn(Optional.of(member1));

        // Use doThrow instead of when().thenThrow()
        Mockito.doThrow(new IllegalArgumentException("Error"))
               .when(teamService).removeMemberFromTeam(any(), any());

        // Act & Assert
        mockMvc.perform(put("/api/teams/team-1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateTeamMembers_WhenIllegalStateException_ReturnsConflict() throws Exception {
        // Arrange
        TeamMemberUpdateRequest request = new TeamMemberUpdateRequest(
                Arrays.asList(member1.getId().value())
        );

        when(teamService.getTeamById(new TeamId("team-1"))).thenReturn(Optional.of(team1));
        when(memberService.getMemberById(member1.getId())).thenReturn(Optional.of(member1));
        when(teamService.removeMemberFromTeam(any(), any())).thenThrow(new IllegalStateException("Error"));

        // Act & Assert
        mockMvc.perform(put("/api/teams/team-1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
