package com.ddd.praha.infrastructure;

import com.ddd.praha.TestcontainersConfiguration;
import com.ddd.praha.domain.*;
import com.ddd.praha.config.RabbitMQConfig;
import com.ddd.praha.domain.entity.Member;
import com.ddd.praha.domain.entity.Team;
import com.ddd.praha.domain.model.Email;
import com.ddd.praha.domain.model.EnrollmentStatus;
import com.ddd.praha.domain.model.MemberName;
import com.ddd.praha.domain.model.TeamName;
import com.ddd.praha.domain.model.TeamNotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NotificationRepositoryImplのRabbitMQ機能テスト
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class NotificationRepositoryImplTest {

    @Autowired
    private NotificationRepositoryImpl notificationRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        // テスト前にキューをクリア
        rabbitTemplate.execute(channel -> {
            channel.queuePurge(RabbitMQConfig.TEAM_NOTIFICATION_QUEUE);
            return null;
        });
    }

    @Test
    void sendToQueue_StringMessage_SendsSuccessfully() throws InterruptedException {
        // Given
        String testMessage = "Test notification message";

        // When
        notificationRepository.sendToQueue(testMessage);

        // Then
        // 短時間待機してメッセージが送信されることを確認
        TimeUnit.MILLISECONDS.sleep(100);
        
        String receivedMessage = (String) rabbitTemplate.receiveAndConvert(RabbitMQConfig.TEAM_NOTIFICATION_QUEUE);
        assertEquals(testMessage, receivedMessage);
    }

    @Test
    void sendNotification_MergeFailure_SendsStructuredMessage() throws InterruptedException {
        // Given
        Member member1 = createTestMember("テスト太郎", "test1@example.com");
        Member member2 = createTestMember("テスト次郎", "test2@example.com");
        Team team = createTestTeam("テストチーム", Arrays.asList(member1, member2));
        // 1名まで削除して合流失敗の状況を作る
        team.deleteMember(member2);
        TeamNotificationEvent event = TeamNotificationEvent.mergeFailure(team, member1);

        // When
        notificationRepository.sendNotification(event);

        // Then
        // 短時間待機してメッセージが送信されることを確認
        TimeUnit.MILLISECONDS.sleep(100);
        
        Object receivedMessage = rabbitTemplate.receiveAndConvert(RabbitMQConfig.TEAM_NOTIFICATION_QUEUE);
        assertNotNull(receivedMessage);
        
        // 構造化されたメッセージが送信されることを確認
      assertInstanceOf(NotificationMessage.class, receivedMessage);
        NotificationMessage notification =
            (NotificationMessage) receivedMessage;
        
        assertEquals("MERGE_FAILURE", notification.type());
        assertNotNull(notification.message());
        assertEquals(team.getId().value(), notification.teamId());
        assertEquals(team.getName().value(), notification.teamName());
        assertEquals(member1.getId().value(), notification.memberId());
        assertEquals(member1.getName().value(), notification.memberName());
        assertTrue(notification.timestamp() > 0);
    }

    @Test
    void sendNotification_TeamSplit_DoesNotSendToQueue() throws InterruptedException {
        // Given
        Member member1 = createTestMember("メンバー1", "member1@example.com");
        Member member2 = createTestMember("メンバー2", "member2@example.com");
        Member member3 = createTestMember("メンバー3", "member3@example.com");
        Member member4 = createTestMember("メンバー4", "member4@example.com");
        Team originalTeam = createTestTeam("元チーム", Arrays.asList(member1, member2));
        Team newTeam = createTestTeam("新チーム", Arrays.asList(member3, member4));
        TeamNotificationEvent event = TeamNotificationEvent.teamSplit(originalTeam, newTeam);

        // When
        notificationRepository.sendNotification(event);

        // Then
        // 短時間待機
        TimeUnit.MILLISECONDS.sleep(100);
        
        // TEAM_SPLITはキューに送信されないことを確認
        Object receivedMessage = rabbitTemplate.receiveAndConvert(RabbitMQConfig.TEAM_NOTIFICATION_QUEUE);
        assertNull(receivedMessage);
    }

    @Test
    void sendNotification_MonitoringRequired_DoesNotSendToQueue() throws InterruptedException {
        // Given
        Member member1 = createTestMember("テスト太郎", "test1@example.com");
        Member member2 = createTestMember("テスト次郎", "test2@example.com");
        Team team = createTestTeam("テストチーム", Arrays.asList(member1, member2));
        TeamNotificationEvent event = TeamNotificationEvent.monitoringRequired(team, member1);

        // When
        notificationRepository.sendNotification(event);

        // Then
        // 短時間待機
        TimeUnit.MILLISECONDS.sleep(100);
        
        // MONITORING_REQUIREDはキューに送信されないことを確認
        Object receivedMessage = rabbitTemplate.receiveAndConvert(RabbitMQConfig.TEAM_NOTIFICATION_QUEUE);
        assertNull(receivedMessage);
    }

    @Test
    void sendEmail_LogsMessage() {
        // Given
        String to = "test@example.com";
        String subject = "テスト件名";
        String body = "テスト本文";

        // When & Then
        // 例外が発生しないことを確認
        assertDoesNotThrow(() -> notificationRepository.sendEmail(to, subject, body));
    }

    private Member createTestMember(String name, String email) {
        return new Member(new MemberName(name), new Email(email), EnrollmentStatus.在籍中);
    }

    private Team createTestTeam(String name, java.util.List<Member> members) {
        return new Team(new TeamName(name), members);
    }
}