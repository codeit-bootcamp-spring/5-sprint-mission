package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

  @Mock ChannelRepository channelRepository;
  @Mock ReadStatusRepository readStatusRepository;
  @Mock MessageRepository messageRepository;
  @Mock UserRepository userRepository;
  @Mock ChannelMapper channelMapper;

  @InjectMocks BasicChannelService channelService;

  // fixtures
  UUID channelId;
  User user;
  Channel publicChannel;
  Channel privateChannel;
  ReadStatus readStatus;

  @BeforeEach
  void setUp() {
    channelId = UUID.randomUUID();

    user = new User("u1", "u1@test.com", "12341234", null);
    ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

    publicChannel = new Channel(ChannelType.PUBLIC, "c1", "d1");
    ReflectionTestUtils.setField(publicChannel, "id", channelId);

    privateChannel = new Channel(ChannelType.PRIVATE, null, null);
    ReflectionTestUtils.setField(privateChannel, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(privateChannel, "createdAt", Instant.now());

    readStatus = new ReadStatus(user, privateChannel, privateChannel.getCreatedAt());
  }

  // ---------- create (PUBLIC) ----------
  @Test
  @DisplayName("public 채널 생성 테스트")
  void createPublicChannel() {
    PublicChannelCreateRequest req = new PublicChannelCreateRequest("c1", "d1");

    ChannelDto dto = new ChannelDto(
        publicChannel.getId(),
        publicChannel.getType(),
        publicChannel.getName(),
        publicChannel.getDescription(),
        null,
        null
    );

    given(channelRepository.save(any(Channel.class))).willReturn(publicChannel);
    given(messageRepository.findLastMessageAtByChannelId(any())).willReturn(Optional.empty());
    given(readStatusRepository.findAllByChannelIdWithUser(any())).willReturn(List.of());
    // 매퍼는 Channel 한 개만 받는 것으로 가정
    given(channelMapper.toDto(publicChannel)).willReturn(dto);

    ChannelDto result = channelService.create(req);

    assertThat(result).isEqualTo(dto);
    verify(channelRepository, times(1)).save(any());
    verify(messageRepository, times(1)).findLastMessageAtByChannelId(any());
    verify(readStatusRepository, times(1)).findAllByChannelIdWithUser(any());
    verify(channelMapper, times(1)).toDto(publicChannel);
  }

  // ---------- create (PRIVATE) ----------
  @Test
  @DisplayName("private 채널 생성 테스트")
  void createPrivateChannel() {
    UUID participantId = user.getId();
    PrivateChannelCreateRequest req = new PrivateChannelCreateRequest(List.of(participantId));

    ChannelDto dto = new ChannelDto(
        privateChannel.getId(),
        ChannelType.PRIVATE,
        null,
        null,
        List.of(new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, false)),
        null
    );

    given(userRepository.findById(eq(participantId))).willReturn(Optional.of(user));
    given(channelRepository.save(any(Channel.class))).willReturn(privateChannel);
    given(readStatusRepository.save(any(ReadStatus.class))).willReturn(readStatus);
    given(messageRepository.findLastMessageAtByChannelId(any())).willReturn(Optional.empty());
    given(readStatusRepository.findAllByChannelIdWithUser(any())).willReturn(List.of(readStatus));
    given(channelMapper.toDto(privateChannel)).willReturn(dto);

    ChannelDto result = channelService.create(req);

    assertThat(result).isEqualTo(dto);
    verify(userRepository, times(1)).findById(participantId);
    verify(channelRepository, times(1)).save(any());
    verify(readStatusRepository, times(1)).save(any());
    verify(readStatusRepository, times(1)).findAllByChannelIdWithUser(any());
    verify(messageRepository, times(1)).findLastMessageAtByChannelId(any());
    verify(channelMapper, times(1)).toDto(privateChannel);
  }

  @Test
  @DisplayName("private 채널 생성 실패 - 존재하지 않는 유저")
  void createPrivateChannel_userNotFound() {
    UUID unknown = UUID.randomUUID();
    PrivateChannelCreateRequest req = new PrivateChannelCreateRequest(List.of(unknown));

    given(userRepository.findById(eq(unknown))).willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.create(req))
        .isInstanceOf(UserNotFoundException.class);
  }

  // ---------- update ----------
  @Test
  @DisplayName("public 채널 업데이트")
  void updatePublicChannel() {
    PublicChannelUpdateRequest req = new PublicChannelUpdateRequest("new", "new");
    Channel updated = new Channel(ChannelType.PUBLIC, "new", "new");
    ReflectionTestUtils.setField(updated, "id", publicChannel.getId());

    ChannelDto dto = new ChannelDto(
        updated.getId(),
        updated.getType(),
        updated.getName(),
        updated.getDescription(),
        null,
        null
    );

    given(channelRepository.findById(eq(publicChannel.getId())))
        .willReturn(Optional.of(publicChannel));
    given(channelRepository.save(any(Channel.class))).willReturn(updated);
    given(channelMapper.toDto(updated)).willReturn(dto);

    ChannelDto result = channelService.update(publicChannel.getId(), req);

    assertThat(result).isEqualTo(dto);
    verify(channelRepository, times(1)).findById(publicChannel.getId());
    verify(channelRepository, times(1)).save(any());
    verify(channelMapper, times(1)).toDto(updated);
  }

  @Test
  @DisplayName("채널 업데이트 실패 - 채널 없음")
  void updateChannel_notFound() {
    given(channelRepository.findById(any())).willReturn(Optional.empty());
    assertThatThrownBy(() -> channelService.update(UUID.randomUUID(),
        new PublicChannelUpdateRequest("n","d")))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("채널 업데이트 실패 - PRIVATE 채널은 수정 불가")
  void updateChannel_private() {
    given(channelRepository.findById(eq(privateChannel.getId())))
        .willReturn(Optional.of(privateChannel));
    assertThatThrownBy(() -> channelService.update(privateChannel.getId(),
        new PublicChannelUpdateRequest("n","d")))
        .isInstanceOf(PrivateChannelUpdateException.class);
  }

  // ---------- delete ----------
  @Test
  @DisplayName("채널 삭제 테스트 (연관 데이터 정리)")
  void deleteChannel() {
    given(channelRepository.findById(eq(publicChannel.getId())))
        .willReturn(Optional.of(publicChannel));

    channelService.delete(publicChannel.getId());

    verify(channelRepository, times(1)).findById(publicChannel.getId());
    verify(readStatusRepository, times(1)).deleteAllByChannelId(publicChannel.getId());
    verify(messageRepository, times(1)).deleteAllByChannelId(publicChannel.getId());
    verify(channelRepository, times(1)).deleteById(publicChannel.getId());
  }

  @Test
  @DisplayName("채널 삭제 실패 - 채널 없음")
  void deleteChannel_notFound() {
    given(channelRepository.findById(any())).willReturn(Optional.empty());
    assertThatThrownBy(() -> channelService.delete(UUID.randomUUID()))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  // ---------- findAllByUserId ----------
  @Test
  @DisplayName("userId가 null이면 PUBLIC 채널만 반환")
  void findAll_publicOnly_whenUserIdNull() {
    ChannelDto publicDto = new ChannelDto(
        publicChannel.getId(), publicChannel.getType(),
        publicChannel.getName(), publicChannel.getDescription(), null, null);

    given(readStatusRepository.findAllByUserId(any())).willReturn(List.of());
    given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList()))
        .willReturn(List.of(publicChannel));
    given(readStatusRepository.findAllByChannelIdWithUser(any())).willReturn(List.of());
    given(messageRepository.findLastMessageAtByChannelId(any())).willReturn(Optional.empty());
    given(channelMapper.toDto(publicChannel)).willReturn(publicDto);

    List<ChannelDto> result = channelService.findAllByUserId(null);

    assertThat(result).containsExactly(publicDto);
    verify(channelRepository, times(1)).findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList());
    verify(channelMapper, times(1)).toDto(publicChannel);
  }

  @Test
  @DisplayName("userId가 있으면 PRIVATE 포함")
  void findAll_includePrivate_whenUserIdGiven() {
    ChannelDto publicDto = new ChannelDto(
        publicChannel.getId(), publicChannel.getType(),
        publicChannel.getName(), publicChannel.getDescription(), null, null);
    ChannelDto privateDto = new ChannelDto(
        privateChannel.getId(), privateChannel.getType(),
        null, null,
        List.of(new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, false)),
        null);

    given(readStatusRepository.findAllByUserId(eq(user.getId())))
        .willReturn(List.of(readStatus));
    given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList()))
        .willReturn(List.of(publicChannel, privateChannel));
    given(readStatusRepository.findAllByChannelIdWithUser(eq(publicChannel.getId())))
        .willReturn(List.of());
    given(readStatusRepository.findAllByChannelIdWithUser(eq(privateChannel.getId())))
        .willReturn(List.of(readStatus));
    given(messageRepository.findLastMessageAtByChannelId(any()))
        .willReturn(Optional.empty());
    given(channelMapper.toDto(publicChannel)).willReturn(publicDto);
    given(channelMapper.toDto(privateChannel)).willReturn(privateDto);

    List<ChannelDto> result = channelService.findAllByUserId(user.getId());

    assertThat(result).containsExactlyInAnyOrder(publicDto, privateDto);
    verify(channelRepository, times(1)).findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList());
    verify(channelMapper, times(1)).toDto(publicChannel);
    verify(channelMapper, times(1)).toDto(privateChannel);
  }
}
