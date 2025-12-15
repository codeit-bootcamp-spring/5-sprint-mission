package com.sprint.mission.discodeit.readstatus.application;

import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatus;
import com.sprint.mission.discodeit.readstatus.presentation.dto.ReadStatusDto;
import com.sprint.mission.discodeit.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReadStatusMapper 단위 테스트")
class ReadStatusMapperTest {

    private final ReadStatusMapper mapper = new ReadStatusMapper();

    private static final UUID TEST_READ_STATUS_ID = UUID.randomUUID();
    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final UUID TEST_CHANNEL_ID = UUID.randomUUID();
    private static final Instant TEST_LAST_READ_AT = Instant.now();

    private User testUser;
    private Channel testChannel;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "$2a$10$encrypted", null);
        ReflectionTestUtils.setField(testUser, "id", TEST_USER_ID);

        testChannel = new Channel(ChannelType.PUBLIC, "general", "General channel");
        ReflectionTestUtils.setField(testChannel, "id", TEST_CHANNEL_ID);
    }

    @Test
    @DisplayName("알림 활성화된 ReadStatus를 ReadStatusDto로 변환 성공")
    void toDto_withNotificationEnabled_returnsDto() {
        // given
        ReadStatus readStatus = new ReadStatus(testUser, testChannel, TEST_LAST_READ_AT, true);
        ReflectionTestUtils.setField(readStatus, "id", TEST_READ_STATUS_ID);

        // when
        ReadStatusDto result = mapper.toDto(readStatus);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(TEST_READ_STATUS_ID);
        assertThat(result.userId()).isEqualTo(TEST_USER_ID);
        assertThat(result.channelId()).isEqualTo(TEST_CHANNEL_ID);
        assertThat(result.lastReadAt()).isEqualTo(TEST_LAST_READ_AT);
        assertThat(result.notificationEnabled()).isTrue();
    }

    @Test
    @DisplayName("알림 비활성화된 ReadStatus를 ReadStatusDto로 변환 성공")
    void toDto_withNotificationDisabled_returnsDto() {
        // given
        ReadStatus readStatus = new ReadStatus(testUser, testChannel, TEST_LAST_READ_AT, false);
        ReflectionTestUtils.setField(readStatus, "id", TEST_READ_STATUS_ID);

        // when
        ReadStatusDto result = mapper.toDto(readStatus);

        // then
        assertThat(result).isNotNull();
        assertThat(result.notificationEnabled()).isFalse();
    }

    @Test
    @DisplayName("null 입력 시 null 반환")
    void toDto_withNull_returnsNull() {
        // when
        ReadStatusDto result = mapper.toDto(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("PRIVATE 채널의 ReadStatus 변환 성공")
    void toDto_withPrivateChannel_returnsDto() {
        // given
        Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
        ReflectionTestUtils.setField(privateChannel, "id", TEST_CHANNEL_ID);

        ReadStatus readStatus = new ReadStatus(testUser, privateChannel, TEST_LAST_READ_AT, true);
        ReflectionTestUtils.setField(readStatus, "id", TEST_READ_STATUS_ID);

        // when
        ReadStatusDto result = mapper.toDto(readStatus);

        // then
        assertThat(result).isNotNull();
        assertThat(result.channelId()).isEqualTo(TEST_CHANNEL_ID);
    }
}
