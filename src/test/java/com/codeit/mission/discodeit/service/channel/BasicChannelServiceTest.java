package com.codeit.mission.discodeit.service.channel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;

import com.codeit.mission.discodeit.dto.data.ChannelDto;
import com.codeit.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.codeit.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.codeit.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.codeit.mission.discodeit.entity.Channel;
import com.codeit.mission.discodeit.entity.ChannelType;
import com.codeit.mission.discodeit.entity.User;
import com.codeit.mission.discodeit.mapper.ChannelMapper;
import com.codeit.mission.discodeit.repository.ChannelRepository;
import com.codeit.mission.discodeit.repository.MessageRepository;
import com.codeit.mission.discodeit.repository.ReadStatusRepository;
import com.codeit.mission.discodeit.repository.UserRepository;
import com.codeit.mission.discodeit.service.basic.BasicChannelService;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @InjectMocks
    private BasicChannelService channelService;

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChannelMapper channelMapper;

    @Test
    @DisplayName("퍼블릭 채널 생성")
    void createPublic() {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("test-channel",
                "description");
        Channel channel = new Channel(ChannelType.PUBLIC, "test-channel", "description");
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC,
                "test-channel", "description", List.of(), Instant.now());

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // when
        ChannelDto createdChannel = channelService.create(request);

        // then
        assertThat(createdChannel).isNotNull();
        assertThat(createdChannel.name()).isEqualTo(request.name());
        then(channelRepository).should().save(any(Channel.class));
    }

    @Test
    @DisplayName("프라이빗 채널 생성")
    void createPrivate() {
        // given
        List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);

        // [수정된 부분] new User() 대신 mock(User.class)를 사용하여 가짜 객체 생성
        List<User> participants = participantIds.stream()
                .map(id -> mock(User.class))
                .collect(Collectors.toList());

        ChannelDto channelDto = new ChannelDto(channel.getId(), ChannelType.PRIVATE, "test-channel",
                "description",
                List.of(), null);

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(userRepository.findAllById(participantIds)).willReturn(participants);
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // when
        ChannelDto createdChannel = channelService.create(request);

        // then
        assertThat(createdChannel).isNotNull();
        assertThat(createdChannel.type()).isEqualTo(ChannelType.PRIVATE);
        then(channelRepository).should().save(any(Channel.class));
        then(userRepository).should().findAllById(participantIds);
        then(readStatusRepository).should().saveAll(anyList());
    }

    @Test
    @DisplayName("사용자별 채널 조회")
    void findAllByUserId() {
        // given
        UUID userId = UUID.randomUUID();
        Channel channel1 = new Channel(ChannelType.PUBLIC, "channel1", "desc1");
        ChannelDto dto1 = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "channel1", "desc1",
                List.of(), Instant.now());

        given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of());
        given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList()))
                .willReturn(Arrays.asList(channel1));
        given(channelMapper.toDto(channel1)).willReturn(dto1);

        // when
        List<ChannelDto> result = channelService.findAllByUserId(userId);

        // then
        assertThat(result).isNotNull();
        then(readStatusRepository).should().findAllByUserId(userId);
    }

    @Test
    @DisplayName("채널 업데이트")
    void update() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("updated-name",
                "updated-desc");
        Channel publicChannel = new Channel(ChannelType.PUBLIC, "old-name", "old-desc");
        ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "updated-name",
                "updated-desc", List.of(), Instant.now());

        given(channelRepository.findById(channelId)).willReturn(
                java.util.Optional.of(publicChannel));
        given(channelMapper.toDto(publicChannel)).willReturn(channelDto);

        // when
        ChannelDto result = channelService.update(channelId, request);

        // then
        assertThat(result).isNotNull();
        then(channelRepository).should().findById(channelId);
    }

    @Test
    @DisplayName("채널 삭제")
    void delete() {
        // given
        UUID channelId = UUID.randomUUID();
        given(channelRepository.existsById(channelId)).willReturn(true);
        willDoNothing().given(messageRepository).deleteAllByChannelId(channelId);
        willDoNothing().given(readStatusRepository).deleteAllByChannelId(channelId);
        willDoNothing().given(channelRepository).deleteById(channelId);

        // when
        channelService.delete(channelId);

        // then
        then(channelRepository).should().existsById(channelId);
        then(messageRepository).should().deleteAllByChannelId(channelId);
        then(readStatusRepository).should().deleteAllByChannelId(channelId);
        then(channelRepository).should().deleteById(channelId);
    }
}