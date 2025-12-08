package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.application.dto.ChannelInfoDto;
import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.channel.presentation.dto.ChannelDto;
import com.sprint.mission.discodeit.user.application.UserMapper;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Nested
    @DisplayName("toDto 메서드")
    class ToDtoTest {

        @Test
        @DisplayName("Channel이 null이면 null을 반환한다")
        void toDto_withNullChannel_returnsNull() {
            // when
            ChannelDto result = channelMapper.toDto(null, List.of(), null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("PUBLIC 채널을 DTO로 변환한다")
        void toDto_withPublicChannel_returnsDto() {
            // given
            Channel channel = new Channel(ChannelType.PUBLIC, "general", "General chat room");
            List<User> participants = List.of();
            Instant lastMessageAt = Instant.now();

            given(userMapper.toDtoList(participants)).willReturn(List.of());

            // when
            ChannelDto result = channelMapper.toDto(channel, participants, lastMessageAt);

            // then
            assertThat(result).isNotNull();
            assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
            assertThat(result.name()).isEqualTo("general");
            assertThat(result.description()).isEqualTo("General chat room");
            assertThat(result.participants()).isEmpty();
            assertThat(result.lastMessageAt()).isEqualTo(lastMessageAt);
        }

        @Test
        @DisplayName("PRIVATE 채널을 참여자와 함께 DTO로 변환한다")
        void toDto_withPrivateChannelAndParticipants_returnsDto() {
            // given
            Channel channel = new Channel(ChannelType.PRIVATE, null, null);
            User user1 = new User("user1", "user1@test.com", "encoded-password1", null);
            User user2 = new User("user2", "user2@test.com", "encoded-password2", null);
            List<User> participants = List.of(user1, user2);

            List<UserDto> userDtos = List.of(
                createUserDto(UUID.randomUUID(), "user1"),
                createUserDto(UUID.randomUUID(), "user2")
            );
            given(userMapper.toDtoList(participants)).willReturn(userDtos);

            // when
            ChannelDto result = channelMapper.toDto(channel, participants, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
            assertThat(result.name()).isNull();
            assertThat(result.description()).isNull();
            assertThat(result.participants()).hasSize(2);
            assertThat(result.lastMessageAt()).isNull();
        }
    }

    @Nested
    @DisplayName("toDtoByInfo 메서드")
    class ToDtoByInfoTest {

        @Test
        @DisplayName("ChannelInfoDto가 null이면 null을 반환한다")
        void toDtoByInfo_withNullInfo_returnsNull() {
            // when
            ChannelDto result = channelMapper.toDtoByInfo(null, List.of(), null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("ChannelInfoDto를 ChannelDto로 변환한다")
        void toDtoByInfo_withInfo_returnsDto() {
            // given
            UUID channelId = UUID.randomUUID();
            ChannelInfoDto infoDto = new ChannelInfoDto(
                channelId,
                ChannelType.PUBLIC,
                "announcements",
                "Announcements channel"
            );
            Instant lastMessageAt = Instant.now();

            given(userMapper.toDtoList(List.of())).willReturn(List.of());

            // when
            ChannelDto result = channelMapper.toDtoByInfo(infoDto, List.of(), lastMessageAt);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(channelId);
            assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
            assertThat(result.name()).isEqualTo("announcements");
            assertThat(result.description()).isEqualTo("Announcements channel");
            assertThat(result.lastMessageAt()).isEqualTo(lastMessageAt);
        }
    }

    @Nested
    @DisplayName("toChannelInfo 메서드")
    class ToChannelInfoTest {

        @Test
        @DisplayName("Channel이 null이면 null을 반환한다")
        void toChannelInfo_withNullChannel_returnsNull() {
            // when
            ChannelInfoDto result = channelMapper.toChannelInfo(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Channel을 ChannelInfoDto로 변환한다")
        void toChannelInfo_withChannel_returnsInfoDto() {
            // given
            Channel channel = new Channel(ChannelType.PUBLIC, "dev", "Dev channel");

            // when
            ChannelInfoDto result = channelMapper.toChannelInfo(channel);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(channel.getId());
            assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
            assertThat(result.name()).isEqualTo("dev");
            assertThat(result.description()).isEqualTo("Dev channel");
        }
    }

    private UserDto createUserDto(UUID id, String username) {
        return new UserDto(
            id,
            username,
            username + "@test.com",
            null,
            false,
            Role.USER
        );
    }
}
