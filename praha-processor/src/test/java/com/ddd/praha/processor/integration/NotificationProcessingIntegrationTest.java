package com.ddd.praha.processor.integration;

import com.ddd.praha.processor.TestcontainersConfiguration;
import com.ddd.praha.processor.dto.NotificationMessage;
import com.ddd.praha.processor.service.NotificationProcessorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@DisplayName("通知処理の統合テスト")
class NotificationProcessingIntegrationTest {

    @Autowired
    private NotificationProcessorService processorService;

    @Test
    @DisplayName("Spring Contextで通知処理サービスが正常に動作する")
    void testNotificationProcessingInSpringContext() {
        // Given
        NotificationMessage message = new NotificationMessage(
            "TEAM_SPLIT",
            "統合テスト用メッセージ",
            "team-test-001",
            "テストチーム",
            "member-test-001",
            "テストユーザー",
            System.currentTimeMillis()
        );

        // When & Then
        assertDoesNotThrow(() -> processorService.processNotification(message));
    }

    @Test
    @DisplayName("すべてのメッセージタイプが正常に処理される")
    void testAllMessageTypesProcessing() {
        // Given
        String[] messageTypes = {"TEAM_SPLIT", "TEAM_MERGED", "MONITORING_REQUIRED", "MERGE_FAILURE"};
        
        for (String type : messageTypes) {
            NotificationMessage message = new NotificationMessage(
                type,
                "統合テスト用メッセージ: " + type,
                "team-test-001",
                "テストチーム",
                "member-test-001", 
                "テストユーザー",
                System.currentTimeMillis()
            );

            // When & Then
            assertDoesNotThrow(() -> processorService.processNotification(message),
                "メッセージタイプ " + type + " の処理でエラーが発生しました");
        }
    }

    @Test
    @DisplayName("サービスが正しく注入されている")
    void testServiceInjection() {
        assertNotNull(processorService, "NotificationProcessorServiceが注入されていません");
    }
}