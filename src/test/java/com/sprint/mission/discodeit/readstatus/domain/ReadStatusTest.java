package com.sprint.mission.discodeit.readstatus.domain;

import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ReadStatus 단위 테스트")
class ReadStatusTest {

    private User user;
    private Channel channel;
    private Instant lastReadAt;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@test.com", "password1234", null);
        channel = new Channel(ChannelType.PUBLIC, "general", "General channel");
        lastReadAt = Instant.now();
    }

    @Nested
    @DisplayName("생성자")
    @SuppressWarnings("DataFlowIssue")
    class ConstructorTest {

        @Test
        @DisplayName("유효한 값으로 ReadStatus 생성 성공")
        void constructor_withValidValues_createsReadStatus() {
            // when
            ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt, true);

            // then
            assertThat(readStatus.getUser()).isEqualTo(user);
            assertThat(readStatus.getChannel()).isEqualTo(channel);
            assertThat(readStatus.getLastReadAt()).isEqualTo(lastReadAt);
            assertThat(readStatus.isNotificationEnabled()).isTrue();
        }

        @Test
        @DisplayName("notificationEnabled가 false인 ReadStatus 생성 성공")
        void constructor_withNotificationDisabled_createsReadStatus() {
            // when
            ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt, false);

            // then
            assertThat(readStatus.isNotificationEnabled()).isFalse();
        }

        @Test
        @DisplayName("user가 null이면 예외 발생")
        void constructor_withNullUser_throwsException() {
            assertThatThrownBy(() -> new ReadStatus(null, channel, lastReadAt, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("user must not be null");
        }

        @Test
        @DisplayName("channel이 null이면 예외 발생")
        void constructor_withNullChannel_throwsException() {
            assertThatThrownBy(() -> new ReadStatus(user, null, lastReadAt, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("channel must not be null");
        }

        @Test
        @DisplayName("lastReadAt이 null이면 예외 발생")
        void constructor_withNullLastReadAt_throwsException() {
            assertThatThrownBy(() -> new ReadStatus(user, channel, null, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("lastReadAt must not be null");
        }
    }

    @Nested
    @DisplayName("update 메서드")
    class UpdateTest {

        @Test
        @DisplayName("lastReadAt과 notificationEnabled 모두 변경 성공")
        void update_withBothValues_updatesBoth() {
            // given
            ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt, true);
            Instant newLastReadAt = lastReadAt.plusSeconds(3600);

            // when
            readStatus.update(newLastReadAt, false);

            // then
            assertThat(readStatus.getLastReadAt()).isEqualTo(newLastReadAt);
            assertThat(readStatus.isNotificationEnabled()).isFalse();
        }

        @Test
        @DisplayName("lastReadAt만 변경 성공")
        void update_withOnlyLastReadAt_updatesLastReadAt() {
            // given
            ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt, true);
            Instant newLastReadAt = lastReadAt.plusSeconds(3600);

            // when
            readStatus.update(newLastReadAt, null);

            // then
            assertThat(readStatus.getLastReadAt()).isEqualTo(newLastReadAt);
            assertThat(readStatus.isNotificationEnabled()).isTrue();
        }

        @Test
        @DisplayName("notificationEnabled만 변경 성공")
        void update_withOnlyNotificationEnabled_updatesNotificationEnabled() {
            // given
            ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt, true);

            // when
            readStatus.update(null, false);

            // then
            assertThat(readStatus.getLastReadAt()).isEqualTo(lastReadAt);
            assertThat(readStatus.isNotificationEnabled()).isFalse();
        }

        @Test
        @DisplayName("둘 다 null이면 기존 값 유지")
        void update_withBothNull_keepsOriginalValues() {
            // given
            ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt, true);

            // when
            readStatus.update(null, null);

            // then
            assertThat(readStatus.getLastReadAt()).isEqualTo(lastReadAt);
            assertThat(readStatus.isNotificationEnabled()).isTrue();
        }

        @Test
        @DisplayName("자기 자신을 반환 (fluent API)")
        void update_returnsItself() {
            // given
            ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt, true);

            // when
            ReadStatus result = readStatus.update(lastReadAt.plusSeconds(1), false);

            // then
            assertThat(result).isSameAs(readStatus);
        }

        @Test
        @DisplayName("notificationEnabled를 false에서 true로 변경")
        void update_withNotificationEnabledTrueFromFalse_updatesNotificationEnabled() {
            // given
            ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt, false);

            // when
            readStatus.update(null, true);

            // then
            assertThat(readStatus.isNotificationEnabled()).isTrue();
        }

        @Test
        @DisplayName("동일한 값으로 update 호출 시 값 유지")
        void update_withSameValues_keepsValues() {
            // given
            ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt, true);

            // when
            readStatus.update(lastReadAt, true);

            // then
            assertThat(readStatus.getLastReadAt()).isEqualTo(lastReadAt);
            assertThat(readStatus.isNotificationEnabled()).isTrue();
        }
    }
}
