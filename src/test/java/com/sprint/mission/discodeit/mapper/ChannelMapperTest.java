package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.data.ChannelDto;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChannelMapper 단위 테스트")
class ChannelMapperTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ChannelMapper channelMapper;

    private Channel createChannelWithId(UUID id, ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        ReflectionTestUtils.setField(channel, "id", id);
        return channel;
    }

    private User createUserWithId(UUID id, String username, String email) {
        User user = new User(username, email, "encodedPassword123456", null);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private UserDto createUserDto(UUID id, String username, String email) {
        return new UserDto(id, username, email, null, false, Role.USER);
    }

    @Test
    @DisplayName("PUBLIC 채널을 DTO로 변환한다")
    void toDto_PublicChannel() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = createChannelWithId(channelId, ChannelType.PUBLIC, "general", "일반 채널");
        Instant lastMessageAt = Instant.now();

        // when
        ChannelDto result = channelMapper.toDto(channel, List.of(), lastMessageAt);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(channelId);
        assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
        assertThat(result.name()).isEqualTo("general");
        assertThat(result.description()).isEqualTo("일반 채널");
        assertThat(result.participants()).isEmpty();
        assertThat(result.lastMessageAt()).isEqualTo(lastMessageAt);
    }

    @Test
    @DisplayName("PRIVATE 채널을 참여자와 함께 DTO로 변환한다")
    void toDto_PrivateChannelWithParticipants() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();

        Channel channel = createChannelWithId(channelId, ChannelType.PRIVATE, null, null);
        User user1 = createUserWithId(user1Id, "user1", "user1@example.com");
        User user2 = createUserWithId(user2Id, "user2", "user2@example.com");
        List<User> participants = List.of(user1, user2);

        UserDto userDto1 = createUserDto(user1Id, "user1", "user1@example.com");
        UserDto userDto2 = createUserDto(user2Id, "user2", "user2@example.com");

        given(userMapper.toDto(user1)).willReturn(userDto1);
        given(userMapper.toDto(user2)).willReturn(userDto2);

        // when
        ChannelDto result = channelMapper.toDto(channel, participants, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(channelId);
        assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
        assertThat(result.name()).isNull();
        assertThat(result.description()).isNull();
        assertThat(result.participants()).hasSize(2);
        assertThat(result.participants()).containsExactly(userDto1, userDto2);
        assertThat(result.lastMessageAt()).isNull();
    }

    @Test
    @DisplayName("참여자가 null이면 빈 리스트를 반환한다")
    void toDto_NullParticipants() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = createChannelWithId(channelId, ChannelType.PUBLIC, "test", null);

        // when
        ChannelDto result = channelMapper.toDto(channel, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.participants()).isEmpty();
    }

    @Test
    @DisplayName("lastMessageAt이 null이어도 변환된다")
    void toDto_NullLastMessageAt() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = createChannelWithId(channelId, ChannelType.PUBLIC, "test", null);

        // when
        ChannelDto result = channelMapper.toDto(channel, List.of(), null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.lastMessageAt()).isNull();
    }

    @Test
    @DisplayName("null 채널을 변환하면 null을 반환한다")
    void toDto_NullChannel() {
        // when
        ChannelDto result = channelMapper.toDto(null, List.of(), Instant.now());

        // then
        assertThat(result).isNull();
    }
}
