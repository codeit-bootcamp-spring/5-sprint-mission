package com.sprint.mission.discodeit.notification.domain;

import com.sprint.mission.discodeit.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Notification 단위 테스트")
class NotificationTest {

    private static final int TITLE_MAX_LENGTH = 100;
    private static final int CONTENT_MAX_LENGTH = 500;

    private User receiver;

    @BeforeEach
    void setUp() {
        receiver = new User("testuser", "test@test.com", "password1234", null);
    }

    @Nested
    @DisplayName("생성자")
    @SuppressWarnings("DataFlowIssue")
    class ConstructorTest {

        @Test
        @DisplayName("유효한 값으로 Notification 생성 성공")
        void constructor_withValidValues_createsNotification() {
            // when
            Notification notification = new Notification(receiver, "Title", "Content");

            // then
            assertThat(notification.getReceiver()).isEqualTo(receiver);
            assertThat(notification.getTitle()).isEqualTo("Title");
            assertThat(notification.getContent()).isEqualTo("Content");
            assertThat(notification.isChecked()).isFalse();
        }

        @Test
        @DisplayName("receiver가 null이면 예외 발생")
        void constructor_withNullReceiver_throwsException() {
            assertThatThrownBy(() -> new Notification(null, "Title", "Content"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("title이 null이면 예외 발생")
        void constructor_withNullTitle_throwsException() {
            assertThatThrownBy(() -> new Notification(receiver, null, "Content"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("title이 최대 길이 초과하면 예외 발생")
        void constructor_withTooLongTitle_throwsException() {
            // given
            String longTitle = "a".repeat(TITLE_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> new Notification(receiver, longTitle, "Content"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("title이 최대 길이면 Notification 생성 성공")
        void constructor_withMaxLengthTitle_createsNotification() {
            // given
            String maxTitle = "a".repeat(TITLE_MAX_LENGTH);

            // when
            Notification notification = new Notification(receiver, maxTitle, "Content");

            // then
            assertThat(notification.getTitle()).hasSize(TITLE_MAX_LENGTH);
        }

        @Test
        @DisplayName("title이 빈 문자열이면 Notification 생성 성공")
        void constructor_withEmptyTitle_createsNotification() {
            // when
            Notification notification = new Notification(receiver, "", "Content");

            // then
            assertThat(notification.getTitle()).isEmpty();
        }

        @Test
        @DisplayName("content가 null이면 예외 발생")
        void constructor_withNullContent_throwsException() {
            assertThatThrownBy(() -> new Notification(receiver, "Title", null))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("content가 최대 길이 초과하면 예외 발생")
        void constructor_withTooLongContent_throwsException() {
            // given
            String longContent = "a".repeat(CONTENT_MAX_LENGTH + 1);

            // when & then
            assertThatThrownBy(() -> new Notification(receiver, "Title", longContent))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("content가 최대 길이면 Notification 생성 성공")
        void constructor_withMaxLengthContent_createsNotification() {
            // given
            String maxContent = "a".repeat(CONTENT_MAX_LENGTH);

            // when
            Notification notification = new Notification(receiver, "Title", maxContent);

            // then
            assertThat(notification.getContent()).hasSize(CONTENT_MAX_LENGTH);
        }

        @Test
        @DisplayName("content가 빈 문자열이면 Notification 생성 성공")
        void constructor_withEmptyContent_createsNotification() {
            // when
            Notification notification = new Notification(receiver, "Title", "");

            // then
            assertThat(notification.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("check 메서드")
    class CheckTest {

        @Test
        @DisplayName("check 호출 시 checked가 true로 변경")
        void check_setsCheckedToTrue() {
            // given
            Notification notification = new Notification(receiver, "Title", "Content");
            assertThat(notification.isChecked()).isFalse();

            // when
            notification.check();

            // then
            assertThat(notification.isChecked()).isTrue();
        }

        @Test
        @DisplayName("이미 checked인 상태에서 check 호출 시 checked 유지")
        void check_whenAlreadyChecked_remainsChecked() {
            // given
            Notification notification = new Notification(receiver, "Title", "Content");
            notification.check();
            assertThat(notification.isChecked()).isTrue();

            // when
            notification.check();

            // then
            assertThat(notification.isChecked()).isTrue();
        }

        @Test
        @DisplayName("자기 자신을 반환 (fluent API)")
        void check_returnsItself() {
            // given
            Notification notification = new Notification(receiver, "Title", "Content");

            // when
            Notification result = notification.check();

            // then
            assertThat(result).isSameAs(notification);
        }
    }
}
