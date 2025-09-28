package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ReadStatusServiceTest {

  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusMapper readStatusMapper;

  @InjectMocks
  private BasicReadStatusService service;

  @Test
  void create_success() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    Instant now = Instant.now();
    var req = new ReadStatusCreateRequest(userId, channelId, now);

    var user = mock(User.class);
    var channel = mock(Channel.class);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(readStatusRepository.existsByUserIdAndChannelId(userId, channelId)).willReturn(false);

    given(readStatusRepository.save(any(ReadStatus.class))).willAnswer(inv -> inv.getArgument(0));

    var dto = new ReadStatusDto(UUID.randomUUID(), userId, channelId, now);
    given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(dto);

    var result = service.create(req);

    assertThat(result.userId()).isEqualTo(userId);
    assertThat(result.channelId()).isEqualTo(channelId);
    assertThat(result.lastReadAt()).isEqualTo(now);
    then(readStatusRepository).should().save(any(ReadStatus.class));
  }

  @Test
  void create_fail_userNotFound() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    var req = new ReadStatusCreateRequest(userId, channelId, Instant.now());

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.create(req)).isInstanceOf(UserNotFoundException.class);
  }

  @Test
  void create_fail_channelNotFound() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    var req = new ReadStatusCreateRequest(userId, channelId, Instant.now());

    given(userRepository.findById(userId)).willReturn(Optional.of(mock(User.class)));
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.create(req)).isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  void create_fail_alreadyExists() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    var req = new ReadStatusCreateRequest(userId, channelId, Instant.now());

    given(userRepository.findById(userId)).willReturn(Optional.of(mock(User.class)));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(mock(Channel.class)));
    given(readStatusRepository.existsByUserIdAndChannelId(userId, channelId)).willReturn(true);

    assertThatThrownBy(() -> service.create(req)).isInstanceOf(
        ReadStatusAlreadyExistsException.class);
  }

  @Test
  void find_success() {
    UUID id = UUID.randomUUID();
    var entity = mock(ReadStatus.class);
    given(readStatusRepository.findById(id)).willReturn(Optional.of(entity));

    var dto = new ReadStatusDto(id, UUID.randomUUID(), UUID.randomUUID(), Instant.now());
    given(readStatusMapper.toDto(entity)).willReturn(dto);

    var result = service.find(id);

    assertThat(result).isEqualTo(dto);
  }

  @Test
  void find_fail_notFound() {
    UUID id = UUID.randomUUID();
    given(readStatusRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.find(id)).isInstanceOf(ReadStatusNotFoundException.class);
  }

  @Test
  void findAllByUserId_success() {
    UUID userId = UUID.randomUUID();

    var rs1 = mock(ReadStatus.class);
    var rs2 = mock(ReadStatus.class);
    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(rs1, rs2));

    var d1 = new ReadStatusDto(UUID.randomUUID(), userId, UUID.randomUUID(), Instant.now());
    var d2 = new ReadStatusDto(UUID.randomUUID(), userId, UUID.randomUUID(), Instant.now());
    given(readStatusMapper.toDto(rs1)).willReturn(d1);
    given(readStatusMapper.toDto(rs2)).willReturn(d2);

    var result = service.findAllByUserId(userId);

    assertThat(result).containsExactly(d1, d2);
  }

  @Test
  void findAllByUserId_empty() {
    UUID userId = UUID.randomUUID();
    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of());

    var result = service.findAllByUserId(userId);

    assertThat(result).isEmpty();
  }

  @Test
  void update_success() {
    UUID id = UUID.randomUUID();
    Instant newTime = Instant.now();
    var req = new ReadStatusUpdateRequest(newTime);

    var entity = mock(ReadStatus.class);
    given(readStatusRepository.findById(id)).willReturn(Optional.of(entity));

    var dto = new ReadStatusDto(id, UUID.randomUUID(), UUID.randomUUID(), newTime);
    given(readStatusMapper.toDto(entity)).willReturn(dto);

    var result = service.update(id, req);

    then(entity).should().update(eq(newTime));
    assertThat(result.lastReadAt()).isEqualTo(newTime);
  }

  @Test
  void update_fail_notFound() {
    UUID id = UUID.randomUUID();
    var req = new ReadStatusUpdateRequest(Instant.now());

    given(readStatusRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.update(id, req)).isInstanceOf(
        ReadStatusNotFoundException.class);
  }


  @Test
  void delete_success() {
    UUID id = UUID.randomUUID();
    given(readStatusRepository.existsById(id)).willReturn(true);

    service.delete(id);

    then(readStatusRepository).should().deleteById(id);
  }

  @Test
  void delete_fail_notFound() {
    UUID id = UUID.randomUUID();
    given(readStatusRepository.existsById(id)).willReturn(false);

    assertThatThrownBy(() -> service.delete(id)).isInstanceOf(NoSuchElementException.class);
  }
}
