package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @Mock ChannelRepository channelRepository;
    @Mock ReadStatusRepository readStatusRepository;
    @Mock MessageRepository messageRepository;
    @Mock UserRepository userRepository;
    @Mock ChannelMapper channelMapper;

    @InjectMocks BasicChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성 성공")
    void createPublicChannelSuccess() {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지사항", "공지용 채널");
        Channel channel = new Channel(ChannelType.PUBLIC, "공지사항", "공지용 채널");
        ChannelDto dto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "공지사항", "공지용 채널", List.of(), null);

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(dto);

        // when
        ChannelDto result = channelService.create(request);

        // then
        assertThat(result.name()).isEqualTo("공지사항");
        then(channelRepository).should(times(1)).save(any(Channel.class));
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void createPrivateChannelSuccess() {
        // given
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(UUID.randomUUID(), UUID.randomUUID()));
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        ChannelDto dto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null, null, List.of(), null);

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(userRepository.findAllById(any())).willReturn(List.of()); // 참가자 조회는 빈 리스트로
        given(readStatusRepository.saveAll(any())).willReturn(List.of());
        given(channelMapper.toDto(any(Channel.class))).willReturn(dto);

        // when
        ChannelDto result = channelService.create(request);

        // then
        assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
    }

    @Test
    @DisplayName("채널 조회 성공")
    void findChannelSuccess() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PUBLIC, "공지", "테스트");
        ChannelDto dto = new ChannelDto(channelId, ChannelType.PUBLIC, "공지", "테스트", List.of(), null);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(channelMapper.toDto(channel)).willReturn(dto);

        // when
        ChannelDto result = channelService.find(channelId);

        // then
        assertThat(result.name()).isEqualTo("공지");
    }

    @Test
    @DisplayName("채널 조회 실패 - 없음")
    void findChannelFailByNotFound() {
        // given
        UUID channelId = UUID.randomUUID();
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> channelService.find(channelId))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("공개 채널 수정 성공")
    void updateChannelSuccess() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새 이름", "새 설명");
        Channel channel = new Channel(ChannelType.PUBLIC, "old", "old");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(channelMapper.toDto(channel)).willReturn(
                new ChannelDto(channelId, ChannelType.PUBLIC, "새 이름", "새 설명", List.of(), null)
        );

        // when
        ChannelDto result = channelService.update(channelId, request);

        // then
        assertThat(result.name()).isEqualTo("새 이름");
    }

    @Test
    @DisplayName("채널 수정 실패 - 존재하지 않는 채널")
    void updateChannelFailByNotFound() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새 이름", "새 설명");

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> channelService.update(channelId, request))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("채널 수정 실패 - 비공개 채널 수정 시도")
    void updateChannelFailByPrivate() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새 이름", "새 설명");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        // when & then
        assertThatThrownBy(() -> channelService.update(channelId, request))
                .isInstanceOf(PrivateChannelUpdateException.class);
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void deleteChannelSuccess() {
        // given
        UUID channelId = UUID.randomUUID();
        given(channelRepository.existsById(channelId)).willReturn(true);

        // when
        channelService.delete(channelId);

        // then
        then(channelRepository).should().deleteById(channelId);
    }

    @Test
    @DisplayName("채널 삭제 실패 - 없음")
    void deleteChannelFailByNotFound() {
        // given
        UUID channelId = UUID.randomUUID();
        given(channelRepository.existsById(channelId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> channelService.delete(channelId))
                .isInstanceOf(ChannelNotFoundException.class);
    }
}