package com.ddd.praha.processor.team;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationProcessorService のテスト")
class TeamNotificationProcessorServiceTest {

    @InjectMocks
    private TeamNotificationProcessorService service;

    private TeamNotificationMessage createTestMessage(String type, String message, String teamId, String teamName, String memberId, String memberName) {
        return new TeamNotificationMessage(
            type,
            message,
            teamId,
            teamName,
            memberId,
            memberName,
            System.currentTimeMillis()
        );
    }

    @Test
    @DisplayName("TEAM_SPLITメッセージを正しく処理できる")
    void testProcessTeamSplitMessage() {
        // Given
        TeamNotificationMessage message = createTestMessage(
            "TEAM_SPLIT",
            "チーム 'チームA' が分割されました。新チーム: 'チームB'",
            "team-001",
            "チームA",
            null,
            null
        );

        // When & Then
        assertDoesNotThrow(() -> service.processNotification(message));
    }

    @Test
    @DisplayName("TEAM_MERGEDメッセージを正しく処理できる")
    void testProcessTeamMergedMessage() {
        // Given
        TeamNotificationMessage message = createTestMessage(
            "TEAM_MERGED",
            "メンバー '田中太郎' がチーム 'チームA' に合流しました",
            "team-001",
            "チームA",
            "member-001",
            "田中太郎"
        );

        // When & Then
        assertDoesNotThrow(() -> service.processNotification(message));
    }

    @Test
    @DisplayName("MONITORING_REQUIREDメッセージを正しく処理できる")
    void testProcessMonitoringRequiredMessage() {
        // Given
        TeamNotificationMessage message = createTestMessage(
            "MONITORING_REQUIRED",
            "チームが2名以下になりました。早急な対応が必要です。",
            "team-001",
            "チームA",
            "member-001",
            "田中太郎"
        );

        // When & Then
        assertDoesNotThrow(() -> service.processNotification(message));
    }

    @Test
    @DisplayName("MERGE_FAILUREメッセージを正しく処理できる")
    void testProcessMergeFailureMessage() {
        // Given
        TeamNotificationMessage message = createTestMessage(
            "MERGE_FAILURE",
            "合流先チームが見つかりません。チーム: チームA、合流待ちメンバー: 田中太郎",
            "team-001",
            "チームA",
            "member-001",
            "田中太郎"
        );

        // When & Then
        assertDoesNotThrow(() -> service.processNotification(message));
    }

    @Test
    @DisplayName("未知のメッセージタイプでも例外が発生しない")
    void testProcessUnknownMessageType() {
        // Given
        TeamNotificationMessage message = createTestMessage(
            "UNKNOWN_TYPE",
            "未知のメッセージタイプです",
            "team-001",
            "チームA",
            "member-001",
            "田中太郎"
        );

        // When & Then
        assertDoesNotThrow(() -> service.processNotification(message));
    }

    @Test
    @DisplayName("nullのメッセージでも例外が発生しない")
    void testProcessNullMessage() {
        // Given
        TeamNotificationMessage message = createTestMessage(
            null,
            null,
            null,
            null,
            null,
            null
        );

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> service.processNotification(message));
    }

    @Test
    @DisplayName("メッセージの各フィールドが正しく取得できる")
    void testMessageFieldAccess() {
        // Given
        long timestamp = System.currentTimeMillis();
        TeamNotificationMessage message = new TeamNotificationMessage(
            "TEAM_SPLIT",
            "テストメッセージ",
            "team-001",
            "チームA",
            "member-001",
            "田中太郎",
            timestamp
        );

        // When & Then
        assertEquals("TEAM_SPLIT", message.type());
        assertEquals("テストメッセージ", message.message());
        assertEquals("team-001", message.teamId());
        assertEquals("チームA", message.teamName());
        assertEquals("member-001", message.memberId());
        assertEquals("田中太郎", message.memberName());
        assertEquals(timestamp, message.timestamp());
    }
}