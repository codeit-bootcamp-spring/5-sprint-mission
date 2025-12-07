package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.channel.presentation.dto.ChannelDto;
import com.sprint.mission.discodeit.user.application.UserMapper;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Collections;
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
    private ChannelMapper mapper;

    private static final UUID TEST_CHANNEL_ID = UUID.randomUUID();
    private static final String TEST_CHANNEL_NAME = "general";
    private static final String TEST_DESCRIPTION = "General discussion channel";

    @Test
    @DisplayName("PUBLIC 채널을 ChannelDto로 변환 성공")
    void toDto_withPublicChannel_returnsDto() {
        // given
        Channel channel = new Channel(ChannelType.PUBLIC, TEST_CHANNEL_NAME, TEST_DESCRIPTION);
        ReflectionTestUtils.setField(channel, "id", TEST_CHANNEL_ID);

        Instant lastMessageAt = Instant.now();

        // when
        ChannelDto result = mapper.toDto(channel, Collections.emptyList(), lastMessageAt);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(TEST_CHANNEL_ID);
        assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
        assertThat(result.name()).isEqualTo(TEST_CHANNEL_NAME);
        assertThat(result.description()).isEqualTo(TEST_DESCRIPTION);
        assertThat(result.participants()).isEmpty();
        assertThat(result.lastMessageAt()).isEqualTo(lastMessageAt);
    }

    @Test
    @DisplayName("PRIVATE 채널과 참여자 목록을 ChannelDto로 변환 성공")
    void toDto_withPrivateChannelAndParticipants_returnsDto() {
        // given
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        ReflectionTestUtils.setField(channel, "id", TEST_CHANNEL_ID);

        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        User user1 = createUser(userId1, "user1", "user1@test.com");
        User user2 = createUser(userId2, "user2", "user2@test.com");
        List<User> participants = List.of(user1, user2);

        UserDto userDto1 = new UserDto(userId1, "user1", "user1@test.com", null, true, Role.USER);
        UserDto userDto2 = new UserDto(userId2, "user2", "user2@test.com", null, false, Role.USER);

        given(userMapper.toDtoList(List.of(user1, user2))).willReturn(List.of(userDto1, userDto2));

        // when
        ChannelDto result = mapper.toDto(channel, participants, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(TEST_CHANNEL_ID);
        assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
        assertThat(result.name()).isNull();
        assertThat(result.description()).isNull();
        assertThat(result.participants()).hasSize(2);
        assertThat(result.participants()).containsExactly(userDto1, userDto2);
        assertThat(result.lastMessageAt()).isNull();
    }

    @Test
    @DisplayName("null 채널 입력 시 null 반환")
    void toDto_withNullChannel_returnsNull() {
        // when
        ChannelDto result = mapper.toDto(null, Collections.emptyList(), null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("null 참여자 목록 입력 시 빈 목록 반환")
    void toDto_withNullParticipants_returnsEmptyList() {
        // given
        Channel channel = new Channel(ChannelType.PUBLIC, TEST_CHANNEL_NAME, TEST_DESCRIPTION);
        ReflectionTestUtils.setField(channel, "id", TEST_CHANNEL_ID);

        // when
        ChannelDto result = mapper.toDto(channel, null, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.participants()).isEmpty();
    }

    @Test
    @DisplayName("빈 참여자 목록 입력 시 빈 목록 반환")
    void toDto_withEmptyParticipants_returnsEmptyList() {
        // given
        Channel channel = new Channel(ChannelType.PUBLIC, TEST_CHANNEL_NAME, TEST_DESCRIPTION);
        ReflectionTestUtils.setField(channel, "id", TEST_CHANNEL_ID);

        // when
        ChannelDto result = mapper.toDto(channel, Collections.emptyList(), null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.participants()).isEmpty();
    }

    @Test
    @DisplayName("ChannelInfo를 ChannelDto로 변환 성공")
    void toDto_withChannelInfo_returnsDto() {
        // given
        ChannelInfoDto channelInfoDto = new ChannelInfoDto(
            TEST_CHANNEL_ID, ChannelType.PUBLIC, TEST_CHANNEL_NAME, TEST_DESCRIPTION
        );
        Instant lastMessageAt = Instant.now();

        // when
        ChannelDto result = mapper.toDtoByInfo(channelInfoDto, Collections.emptyList(), lastMessageAt);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(TEST_CHANNEL_ID);
        assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
        assertThat(result.name()).isEqualTo(TEST_CHANNEL_NAME);
        assertThat(result.description()).isEqualTo(TEST_DESCRIPTION);
        assertThat(result.participants()).isEmpty();
        assertThat(result.lastMessageAt()).isEqualTo(lastMessageAt);
    }

    @Test
    @DisplayName("ChannelInfo PRIVATE 채널과 참여자 목록을 ChannelDto로 변환 성공")
    void toDto_withChannelInfoAndParticipants_returnsDto() {
        // given
        ChannelInfoDto channelInfoDto = new ChannelInfoDto(
            TEST_CHANNEL_ID, ChannelType.PRIVATE, null, null
        );

        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        User user1 = createUser(userId1, "user1", "user1@test.com");
        User user2 = createUser(userId2, "user2", "user2@test.com");
        List<User> participants = List.of(user1, user2);

        UserDto userDto1 = new UserDto(userId1, "user1", "user1@test.com", null, true, Role.USER);
        UserDto userDto2 = new UserDto(userId2, "user2", "user2@test.com", null, false, Role.USER);

        given(userMapper.toDtoList(participants)).willReturn(List.of(userDto1, userDto2));

        // when
        ChannelDto result = mapper.toDtoByInfo(channelInfoDto, participants, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(TEST_CHANNEL_ID);
        assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
        assertThat(result.name()).isNull();
        assertThat(result.description()).isNull();
        assertThat(result.participants()).hasSize(2);
        assertThat(result.participants()).containsExactly(userDto1, userDto2);
        assertThat(result.lastMessageAt()).isNull();
    }

    @Test
    @DisplayName("null ChannelInfo 입력 시 null 반환")
    void toDto_withNullChannelInfo_returnsNull() {
        // when
        ChannelDto result = mapper.toDtoByInfo(null, Collections.emptyList(), null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("PUBLIC Channel을 ChannelInfo로 변환 성공")
    void toChannelInfo_withPublicChannel_returnsChannelInfo() {
        // given
        Channel channel = new Channel(ChannelType.PUBLIC, TEST_CHANNEL_NAME, TEST_DESCRIPTION);
        ReflectionTestUtils.setField(channel, "id", TEST_CHANNEL_ID);

        // when
        ChannelInfoDto result = mapper.toChannelInfo(channel);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(TEST_CHANNEL_ID);
        assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
        assertThat(result.name()).isEqualTo(TEST_CHANNEL_NAME);
        assertThat(result.description()).isEqualTo(TEST_DESCRIPTION);
    }

    @Test
    @DisplayName("PRIVATE Channel을 ChannelInfo로 변환 성공")
    void toChannelInfo_withPrivateChannel_returnsChannelInfo() {
        // given
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        ReflectionTestUtils.setField(channel, "id", TEST_CHANNEL_ID);

        // when
        ChannelInfoDto result = mapper.toChannelInfo(channel);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(TEST_CHANNEL_ID);
        assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
        assertThat(result.name()).isNull();
        assertThat(result.description()).isNull();
    }

    @Test
    @DisplayName("null Channel 입력 시 null 반환")
    void toChannelInfo_withNullChannel_returnsNull() {
        // when
        ChannelInfoDto result = mapper.toChannelInfo(null);

        // then
        assertThat(result).isNull();
    }

    private User createUser(UUID id, String username, String email) {
        User user = new User(username, email, "$2a$10$encrypted", null);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
