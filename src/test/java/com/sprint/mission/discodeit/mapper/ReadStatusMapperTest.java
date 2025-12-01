package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.createChannelWithId;
import static com.sprint.mission.discodeit.support.TestFixtures.createReadStatusWithId;
import static com.sprint.mission.discodeit.support.TestFixtures.createUserWithId;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReadStatusMapper 단위 테스트")
class ReadStatusMapperTest {

    private final ReadStatusMapper readStatusMapper = new ReadStatusMapper();

    @Test
    @DisplayName("ReadStatus를 DTO로 변환한다")
    void toDto_Success() {
        // given
        UUID readStatusId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Instant lastReadAt = Instant.now();

        User user = createUserWithId(userId, "testuser");
        Channel channel = createChannelWithId(channelId, ChannelType.PUBLIC, "general", "General channel");
        ReadStatus readStatus = createReadStatusWithId(readStatusId, user, channel, lastReadAt);

        // when
        ReadStatusDto result = readStatusMapper.toDto(readStatus);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(readStatusId);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.channelId()).isEqualTo(channelId);
        assertThat(result.lastReadAt()).isEqualTo(lastReadAt);
    }

    @Test
    @DisplayName("null ReadStatus를 변환하면 null을 반환한다")
    void toDto_NullReadStatus() {
        // when
        ReadStatusDto result = readStatusMapper.toDto(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("PRIVATE 채널의 ReadStatus도 정상 변환된다")
    void toDto_PrivateChannel() {
        // given
        UUID readStatusId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Instant lastReadAt = Instant.now().minusSeconds(3600);

        User user = createUserWithId(userId, "testuser");
        Channel channel = createChannelWithId(channelId, ChannelType.PRIVATE, null, null);
        ReadStatus readStatus = createReadStatusWithId(readStatusId, user, channel, lastReadAt);

        // when
        ReadStatusDto result = readStatusMapper.toDto(readStatus);

        // then
        assertThat(result).isNotNull();
        assertThat(result.channelId()).isEqualTo(channelId);
        assertThat(result.lastReadAt()).isEqualTo(lastReadAt);
    }
}
