package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.application.dto.ChannelInfoDto;
import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import com.sprint.mission.discodeit.user.domain.User;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChannelInfoService 단위 테스트")
class ChannelInfoServiceTest {

    @Mock
    private ChannelMapper channelMapper;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @InjectMocks
    private ChannelInfoService channelInfoService;

    @Nested
    @DisplayName("findPublicChannels 메서드")
    class FindAllPublicChannelsTest {

        @Test
        @DisplayName("PUBLIC 채널 목록을 반환한다")
        void findPublicChannels_returnsPublicChannels() {
            // given
            Channel channel1 = new Channel(ChannelType.PUBLIC, "general", "General chat");
            Channel channel2 = new Channel(ChannelType.PUBLIC, "random", "Random talk");
            List<Channel> channels = List.of(channel1, channel2);

            given(channelRepository.findAllByType(ChannelType.PUBLIC)).willReturn(channels);
            given(channelMapper.toChannelInfo(any(Channel.class)))
                .willAnswer(invocation -> {
                    Channel channel = invocation.getArgument(0);
                    return new ChannelInfoDto(
                        channel.getId(),
                        channel.getType(),
                        channel.getName(),
                        channel.getDescription()
                    );
                });

            // when
            List<ChannelInfoDto> result = channelInfoService.findPublicChannels();

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                .extracting(ChannelInfoDto::name)
                .containsExactlyInAnyOrder("general", "random");
            then(channelRepository).should().findAllByType(ChannelType.PUBLIC);
        }

        @Test
        @DisplayName("PUBLIC 채널이 없으면 빈 리스트를 반환한다")
        void findPublicChannels_withNoChannels_returnsEmptyList() {
            // given
            given(channelRepository.findAllByType(ChannelType.PUBLIC)).willReturn(List.of());

            // when
            List<ChannelInfoDto> result = channelInfoService.findPublicChannels();

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findSubscribedChannels 메서드")
    class FindSubscribedChannelsTest {

        @Test
        @DisplayName("사용자가 구독한 채널 목록을 반환한다")
        void findSubscribedChannels_returnsSubscribedChannels() {
            // given
            UUID userId = UUID.randomUUID();
            User user = new User("testuser", "test@test.com", "encoded-password", null);

            Channel publicChannel = new Channel(ChannelType.PUBLIC, "general", "General chat");
            Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);

            ReadStatus readStatus1 = new ReadStatus(user, publicChannel, Instant.now(), true);
            ReadStatus readStatus2 = new ReadStatus(user, privateChannel, Instant.now(), true);
            List<ReadStatus> readStatuses = List.of(readStatus1, readStatus2);

            given(readStatusRepository.findAllWithChannelByUserId(userId)).willReturn(readStatuses);
            given(channelMapper.toChannelInfo(any(Channel.class)))
                .willAnswer(invocation -> {
                    Channel channel = invocation.getArgument(0);
                    return new ChannelInfoDto(
                        channel.getId(),
                        channel.getType(),
                        channel.getName(),
                        channel.getDescription()
                    );
                });

            // when
            List<ChannelInfoDto> result = channelInfoService.findSubscribedChannels(userId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                .extracting(ChannelInfoDto::type)
                .containsExactlyInAnyOrder(ChannelType.PUBLIC, ChannelType.PRIVATE);
            then(readStatusRepository).should().findAllWithChannelByUserId(userId);
        }

        @Test
        @DisplayName("구독한 채널이 없으면 빈 리스트를 반환한다")
        void findSubscribedChannels_withNoSubscriptions_returnsEmptyList() {
            // given
            UUID userId = UUID.randomUUID();
            given(readStatusRepository.findAllWithChannelByUserId(userId)).willReturn(List.of());

            // when
            List<ChannelInfoDto> result = channelInfoService.findSubscribedChannels(userId);

            // then
            assertThat(result).isEmpty();
        }
    }
}
