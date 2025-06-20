package com.ddd.praha.processor.listener;

import com.ddd.praha.processor.dto.NotificationMessage;
import com.ddd.praha.processor.service.NotificationProcessorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeamNotificationListener のテスト")
class TeamNotificationListenerTest {

    @Mock
    private NotificationProcessorService processorService;

    @InjectMocks
    private TeamNotificationListener listener;

    private NotificationMessage createTestMessage(String type, String message) {
        return new NotificationMessage(
            type,
            message,
            "team-001",
            "チームA",
            "member-001",
            "田中太郎",
            System.currentTimeMillis()
        );
    }

    @Test
    @DisplayName("チーム通知メッセージを正常に処理できる")
    void testHandleTeamNotificationSuccess() {
        // Given
        NotificationMessage message = createTestMessage("TEAM_SPLIT", "チーム分割メッセージ");
        doNothing().when(processorService).processNotification(message);

        // When & Then
        assertDoesNotThrow(() -> listener.handleTeamNotification(message));
        verify(processorService, times(1)).processNotification(message);
    }

    @Test
    @DisplayName("プロセッサーでエラーが発生した場合、例外が再スローされる")
    void testHandleTeamNotificationWithProcessorError() {
        // Given
        NotificationMessage message = createTestMessage("TEAM_SPLIT", "チーム分割メッセージ");
        RuntimeException processorException = new RuntimeException("プロセッサーエラー");
        doThrow(processorException).when(processorService).processNotification(message);

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> listener.handleTeamNotification(message)
        );
        assertEquals("プロセッサーエラー", exception.getMessage());
        verify(processorService, times(1)).processNotification(message);
    }

    @Test
    @DisplayName("異なるタイプのメッセージも正常に処理される")
    void testHandleDifferentMessageTypes() {
        // Given
        NotificationMessage teamSplitMessage = createTestMessage("TEAM_SPLIT", "チーム分割");
        NotificationMessage teamMergedMessage = createTestMessage("TEAM_MERGED", "チーム合流");
        NotificationMessage monitoringMessage = createTestMessage("MONITORING_REQUIRED", "監視必要");
        NotificationMessage mergeFailureMessage = createTestMessage("MERGE_FAILURE", "合流失敗");

        doNothing().when(processorService).processNotification(any(NotificationMessage.class));

        // When & Then
        assertDoesNotThrow(() -> {
            listener.handleTeamNotification(teamSplitMessage);
            listener.handleTeamNotification(teamMergedMessage);
            listener.handleTeamNotification(monitoringMessage);
            listener.handleTeamNotification(mergeFailureMessage);
        });

        verify(processorService, times(4)).processNotification(any(NotificationMessage.class));
    }

    @Test
    @DisplayName("nullメッセージでも適切に処理される")
    void testHandleNullMessage() {
        // Given
        NotificationMessage nullMessage = new NotificationMessage(
            null, null, null, null, null, null, 0L
        );
        doNothing().when(processorService).processNotification(nullMessage);

        // When & Then
        assertDoesNotThrow(() -> listener.handleTeamNotification(nullMessage));
        verify(processorService, times(1)).processNotification(nullMessage);
    }

    @Test
    @DisplayName("複数回の呼び出しが正常に処理される")
    void testMultipleMessageHandling() {
        // Given
        NotificationMessage message1 = createTestMessage("TEAM_SPLIT", "メッセージ1");
        NotificationMessage message2 = createTestMessage("TEAM_MERGED", "メッセージ2");
        NotificationMessage message3 = createTestMessage("MONITORING_REQUIRED", "メッセージ3");

        doNothing().when(processorService).processNotification(any(NotificationMessage.class));

        // When
        listener.handleTeamNotification(message1);
        listener.handleTeamNotification(message2);
        listener.handleTeamNotification(message3);

        // Then
        verify(processorService, times(1)).processNotification(message1);
        verify(processorService, times(1)).processNotification(message2);
        verify(processorService, times(1)).processNotification(message3);
        verify(processorService, times(3)).processNotification(any(NotificationMessage.class));
    }

    @Test
    @DisplayName("プロセッサーサービスが正しく注入されている")
    void testProcessorServiceInjection() {
        // When & Then
        assertNotNull(processorService);
    }
}