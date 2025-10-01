package com.sprint.mission.discodeit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BasicChannelServiceTest {

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

  @InjectMocks
  private BasicChannelService channelService;

  private ChannelDto channelDto;
  private Channel channel;

  private UUID channelId;
  private UUID userId;
  private String name;
  private String description;

  @BeforeEach
  public void setup() {
    channelId = UUID.randomUUID();
    userId = UUID.randomUUID();
    name = "테스트 채널";
    description = "테스트 채널입니다.";

    channel = new Channel(ChannelType.PUBLIC, "테스트 채널", "테스트 채널입니다.");
    channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, name, description, List.of(),
        Instant.now());
  }

  @Test
  @DisplayName("채널 생성 테스트")
  void create_public_channel() {
    PublicChannelCreateRequest req = new PublicChannelCreateRequest(name, description);

    given(channelMapper.toDto(any())).willReturn(channelDto);

    ChannelDto result = channelService.create(req);

    Assertions.assertThat(result).isEqualTo(channelDto);
  }

  @Test
  @DisplayName("비공개 채널 생성 테스트")
  void create_private_channel() {
    PrivateChannelCreateRequest req = new PrivateChannelCreateRequest(List.of(userId));

    given(channelMapper.toDto(any())).willReturn(channelDto);

    ChannelDto result = channelService.create(req);
    Assertions.assertThat(result).isEqualTo(channelDto);
  }

  @Test
  @DisplayName("채널 수정 테스트 성공")
  void update_channel() {
    PublicChannelUpdateRequest req = new PublicChannelUpdateRequest("newName", "newDescription");

    given(channelRepository.findById(any())).willReturn(Optional.of(channel));
    given(channelMapper.toDto(any())).willReturn(channelDto);

    ChannelDto result = channelService.update(channelId, req);

    Assertions.assertThat(result).isEqualTo(channelDto);
  }

  @Test
  @DisplayName("채널 수정 테스트 실패")
  void update_channel_fail() {
    PublicChannelUpdateRequest req = new PublicChannelUpdateRequest("newName", "newDescription");

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> channelService.update(channelId, req))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  //TODO 수정까지 끝
  // delete, findByUserId 메소드

  @Test
  @DisplayName("채널 삭제 테스트 성공")
  void delete_channel() {
    given(channelRepository.existsById(channelId)).willReturn(true);

    channelService.delete(channelId);
    verify(channelRepository).existsById(channelId);

  }

  @Test
  @DisplayName("채널 삭제 테스트 실패")
  void delete_channel_fail() {
    given(channelRepository.existsById(channelId)).willReturn(false);
    Assertions.assertThatThrownBy(() -> channelService.delete(channelId))
        .isInstanceOf(ChannelNotFoundException.class);
  }

//  @Test
//  @DisplayName("findByUserId")
//  void findByUserId() {
//    given(channelRepository.findById(userId)).willReturn(Optional.of(channel));
//    given(channelMapper.toDto(any())).willReturn(channelDto);
//  }
}
