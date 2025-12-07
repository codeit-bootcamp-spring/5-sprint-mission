package com.sprint.mission.discodeit.notification.application;

import com.sprint.mission.discodeit.notification.domain.Notification;
import com.sprint.mission.discodeit.notification.presentation.dto.NotificationDto;
import com.sprint.mission.discodeit.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NotificationMapper 단위 테스트")
class NotificationMapperTest {

    private final NotificationMapper mapper = new NotificationMapper();

    private static final UUID TEST_NOTIFICATION_ID = UUID.randomUUID();
    private static final UUID TEST_RECEIVER_ID = UUID.randomUUID();
    private static final String TEST_TITLE = "New Message";
    private static final String TEST_CONTENT = "You have received a new message";
    private static final Instant TEST_CREATED_AT = Instant.now();

    private User testReceiver;

    @BeforeEach
    void setUp() {
        testReceiver = new User("receiver", "receiver@example.com", "$2a$10$encrypted", null);
        ReflectionTestUtils.setField(testReceiver, "id", TEST_RECEIVER_ID);
    }

    @Test
    @DisplayName("Notification을 NotificationDto로 변환 성공")
    void toDto_withValidNotification_returnsDto() {
        // given
        Notification notification = new Notification(testReceiver, TEST_TITLE, TEST_CONTENT);
        ReflectionTestUtils.setField(notification, "id", TEST_NOTIFICATION_ID);
        ReflectionTestUtils.setField(notification, "createdAt", TEST_CREATED_AT);

        // when
        NotificationDto result = mapper.toDto(notification);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(TEST_NOTIFICATION_ID);
        assertThat(result.createdAt()).isEqualTo(TEST_CREATED_AT);
        assertThat(result.receiverId()).isEqualTo(TEST_RECEIVER_ID);
        assertThat(result.title()).isEqualTo(TEST_TITLE);
        assertThat(result.content()).isEqualTo(TEST_CONTENT);
    }

    @Test
    @DisplayName("null 입력 시 null 반환")
    void toDto_withNull_returnsNull() {
        // when
        NotificationDto result = mapper.toDto(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("긴 제목과 내용의 Notification 변환 성공")
    void toDto_withLongTitleAndContent_returnsDto() {
        // given
        String longTitle = "A".repeat(100);
        String longContent = "B".repeat(500);

        Notification notification = new Notification(testReceiver, longTitle, longContent);
        ReflectionTestUtils.setField(notification, "id", TEST_NOTIFICATION_ID);
        ReflectionTestUtils.setField(notification, "createdAt", TEST_CREATED_AT);

        // when
        NotificationDto result = mapper.toDto(notification);

        // then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo(longTitle);
        assertThat(result.content()).isEqualTo(longContent);
    }

    @Test
    @DisplayName("다른 수신자의 Notification 변환 성공")
    void toDto_withDifferentReceiver_returnsDto() {
        // given
        UUID anotherReceiverId = UUID.randomUUID();
        User anotherReceiver = new User("another", "another@example.com", "$2a$10$encrypted", null);
        ReflectionTestUtils.setField(anotherReceiver, "id", anotherReceiverId);

        Notification notification = new Notification(anotherReceiver, TEST_TITLE, TEST_CONTENT);
        ReflectionTestUtils.setField(notification, "id", TEST_NOTIFICATION_ID);
        ReflectionTestUtils.setField(notification, "createdAt", TEST_CREATED_AT);

        // when
        NotificationDto result = mapper.toDto(notification);

        // then
        assertThat(result).isNotNull();
        assertThat(result.receiverId()).isEqualTo(anotherReceiverId);
    }
}
