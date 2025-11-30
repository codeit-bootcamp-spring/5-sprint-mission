package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.notification.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NotificationMapper 단위 테스트")
class NotificationMapperTest {

    private final NotificationMapper notificationMapper = new NotificationMapper();

    private User createUserWithId(UUID id) {
        User user = new User("testuser", "test@example.com", "encodedPassword123456", null);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Notification createNotificationWithId(UUID id, User receiver, String title, String content) {
        Notification notification = new Notification(receiver, title, content);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", Instant.now());
        return notification;
    }

    @Test
    @DisplayName("Notification을 DTO로 변환한다")
    void toDto_Success() {
        // given
        UUID notificationId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        String title = "New Message";
        String content = "Hello, World!";

        User receiver = createUserWithId(receiverId);
        Notification notification = createNotificationWithId(notificationId, receiver, title, content);

        // when
        NotificationDto result = notificationMapper.toDto(notification);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(notificationId);
        assertThat(result.receiverId()).isEqualTo(receiverId);
        assertThat(result.title()).isEqualTo(title);
        assertThat(result.content()).isEqualTo(content);
        assertThat(result.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("null Notification을 변환하면 null을 반환한다")
    void toDto_NullNotification() {
        // when
        NotificationDto result = notificationMapper.toDto(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("다양한 제목과 내용을 가진 Notification을 변환한다")
    void toDto_VariousContent() {
        // given
        UUID notificationId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        String title = "sender (#general)";
        String content = "This is a longer message content with special characters: @#$%^&*()";

        User receiver = createUserWithId(receiverId);
        Notification notification = createNotificationWithId(notificationId, receiver, title, content);

        // when
        NotificationDto result = notificationMapper.toDto(notification);

        // then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo(title);
        assertThat(result.content()).isEqualTo(content);
    }

    @Test
    @DisplayName("권한 변경 알림을 DTO로 변환한다")
    void toDto_RoleUpdateNotification() {
        // given
        UUID notificationId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        String title = "권한이 변경되었습니다.";
        String content = "USER -> ADMIN";

        User receiver = createUserWithId(receiverId);
        Notification notification = createNotificationWithId(notificationId, receiver, title, content);

        // when
        NotificationDto result = notificationMapper.toDto(notification);

        // then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("권한이 변경되었습니다.");
        assertThat(result.content()).isEqualTo("USER -> ADMIN");
    }
}
