package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto.CreateCommand;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReadStatusServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private ReadStatusMapper mapper;

  @InjectMocks
  private BasicReadStatusService service;

  private User user;
  private Channel channel;
  private ReadStatus entity;
  private ReadStatusDto.Detail detail;
  private CreateCommand command;

  @BeforeEach
  void setUp() {
    user = User.builder()
               .username("reader")
               .password("secret")
               .build();

    channel = Channel.builder()
                     .name("general")
                     .build();

    entity = ReadStatus.builder()
                       .user(user)
                       .channel(channel)
                       .build();

    command = CreateCommand.builder()
                           .userId(user.getId())
                           .channelId(channel.getId())
                           .build();

    detail = ReadStatusDto.Detail.builder()
                                 .id(entity.getId())
                                 .userId(user.getId())
                                 .channelId(channel.getId())
                                 .build();
  }

  @Test
  void create_success() {
    given(userRepository.findById(command.getUserId())).willReturn(Optional.of(user));
    given(channelRepository.findById(command.getChannelId())).willReturn(Optional.of(channel));
    given(readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId()))
        .willReturn(Optional.empty());
    given(mapper.toEntity(command, user, channel)).willReturn(entity);
    given(mapper.toDetail(entity)).willReturn(detail);

    ReadStatusDto.Detail result = service.create(command);

    assertThat(result).isEqualTo(detail);
    then(readStatusRepository).should()
                              .save(entity);
  }

  @Test
  void create_fail_userNotFound() {
    given(userRepository.findById(command.getUserId())).willReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> service.create(command));
  }

  @Test
  void create_fail_channelNotFound() {
    given(userRepository.findById(command.getUserId())).willReturn(Optional.of(user));
    given(channelRepository.findById(command.getChannelId())).willReturn(Optional.empty());

    assertThrows(ChannelNotFoundException.class, () -> service.create(command));
  }

  @Test
  void create_fail_alreadyExists() {
    given(userRepository.findById(command.getUserId())).willReturn(Optional.of(user));
    given(channelRepository.findById(command.getChannelId())).willReturn(Optional.of(channel));
    given(readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId()))
        .willReturn(Optional.of(entity));

    assertThrows(ReadStatusAlreadyExistsException.class, () -> service.create(command));
  }

  @Test
  void find_success() {
    UUID id = UUID.randomUUID();
    given(readStatusRepository.findById(id)).willReturn(Optional.of(entity));
    given(mapper.toDetail(entity)).willReturn(detail);

    ReadStatusDto.Detail result = service.find(id);

    assertThat(result).isEqualTo(detail);
  }

  @Test
  void find_notFound_fail() {
    UUID id = UUID.randomUUID();
    given(readStatusRepository.findById(id)).willReturn(Optional.empty());

    assertThrows(ReadStatusNotFoundException.class, () -> service.find(id));
  }

  @Test
  void findAllByUserId_success() {
    UUID userId = user.getId();
    given(readStatusRepository.findByUserId(userId)).willReturn(List.of(entity));
    given(mapper.toDetail(entity)).willReturn(detail);

    List<ReadStatusDto.Detail> result = service.findAllByUserId(userId);

    assertThat(result).containsExactly(detail);
  }

  @Test
  void delete_success() {
    UUID id = UUID.randomUUID();
    given(readStatusRepository.findById(id)).willReturn(Optional.of(entity));

    service.delete(id);

    then(readStatusRepository).should()
                              .delete(entity);
  }

  @Test
  void delete_notFound_fail() {
    UUID id = UUID.randomUUID();
    given(readStatusRepository.findById(id)).willReturn(Optional.empty());

    assertThrows(ReadStatusNotFoundException.class, () -> service.delete(id));
  }

  @Test
  void deleteAll_success() {
    service.deleteAll();

    then(readStatusRepository).should()
                              .deleteAll();
  }

  @Test
  void update_success() {
    UUID id = UUID.randomUUID();
    given(readStatusRepository.findById(id)).willReturn(Optional.of(entity));
    given(mapper.toDetail(entity)).willReturn(detail);

    ReadStatusDto.Detail result = service.update(id);

    assertThat(result).isEqualTo(detail);
    then(readStatusRepository).should()
                              .save(entity);
  }

  @Test
  void update_notFound_fail() {
    UUID id = UUID.randomUUID();
    given(readStatusRepository.findById(id)).willReturn(Optional.empty());

    assertThrows(ReadStatusNotFoundException.class, () -> service.update(id));
  }
}
