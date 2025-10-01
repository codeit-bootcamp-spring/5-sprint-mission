// src/test/java/com/sprint/mission/discodeit/service/UserStatusServiceTest.java
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.exception.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserStatusServiceTest {

  @Mock
  private UserStatusRepository userStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserStatusMapper userStatusMapper;

  @InjectMocks
  private BasicUserStatusService service;

  @Test
  void create_success() {
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();
    var req = new UserStatusCreateRequest(userId, now);

    var user = mock(User.class);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(user.getStatus()).willReturn(null);

    given(userStatusRepository.save(any(UserStatus.class))).willAnswer(inv -> inv.getArgument(0));

    var dto = new UserStatusDto(UUID.randomUUID(), userId, now);
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(dto);

    var result = service.create(req);

    assertThat(result.userId()).isEqualTo(userId);
    assertThat(result.lastActiveAt()).isEqualTo(now);
    then(userStatusRepository).should().save(any(UserStatus.class));
  }

  @Test
  void create_fail_userNotFound() {
    UUID userId = UUID.randomUUID();
    var req = new UserStatusCreateRequest(userId, Instant.now());

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.create(req)).isInstanceOf(UserNotFoundException.class);
  }

  @Test
  void create_fail_alreadyExists() {
    UUID userId = UUID.randomUUID();
    var req = new UserStatusCreateRequest(userId, Instant.now());

    var user = mock(User.class);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(user.getStatus()).willReturn(mock(UserStatus.class));

    assertThatThrownBy(() -> service.create(req)).isInstanceOf(
        UserStatusAlreadyExistsException.class);
  }

  @Test
  void find_success() {
    UUID statusId = UUID.randomUUID();
    var status = mock(UserStatus.class);
    given(userStatusRepository.findById(statusId)).willReturn(Optional.of(status));

    var dto = new UserStatusDto(statusId, UUID.randomUUID(), Instant.now());
    given(userStatusMapper.toDto(status)).willReturn(dto);

    var result = service.find(statusId);

    assertThat(result).isEqualTo(dto);
  }

  @Test
  void find_fail_notFound() {
    UUID statusId = UUID.randomUUID();
    given(userStatusRepository.findById(statusId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.find(statusId)).isInstanceOf(
        UserStatusNotFoundException.class);
  }

  @Test
  void findAll_success() {
    var s1 = mock(UserStatus.class);
    var s2 = mock(UserStatus.class);
    given(userStatusRepository.findAll()).willReturn(List.of(s1, s2));

    var d1 = new UserStatusDto(UUID.randomUUID(), UUID.randomUUID(), Instant.now());
    var d2 = new UserStatusDto(UUID.randomUUID(), UUID.randomUUID(), Instant.now());
    given(userStatusMapper.toDto(s1)).willReturn(d1);
    given(userStatusMapper.toDto(s2)).willReturn(d2);

    var result = service.findAll();

    assertThat(result).containsExactly(d1, d2);
  }

  @Test
  void findAll_empty() {
    given(userStatusRepository.findAll()).willReturn(List.of());

    var result = service.findAll();

    assertThat(result).isEmpty();
  }

  @Test
  void update_success() {
    UUID statusId = UUID.randomUUID();
    Instant newTime = Instant.now();
    var req = new UserStatusUpdateRequest(newTime);

    var entity = mock(UserStatus.class);
    given(userStatusRepository.findById(statusId)).willReturn(Optional.of(entity));

    var dto = new UserStatusDto(statusId, UUID.randomUUID(), newTime);
    given(userStatusMapper.toDto(entity)).willReturn(dto);

    var result = service.update(statusId, req);

    then(entity).should().update(eq(newTime));
    assertThat(result.lastActiveAt()).isEqualTo(newTime);
  }

  @Test
  void update_fail_notFound() {
    UUID statusId = UUID.randomUUID();
    var req = new UserStatusUpdateRequest(Instant.now());

    given(userStatusRepository.findById(statusId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.update(statusId, req)).isInstanceOf(
        UserStatusNotFoundException.class);
  }

  @Test
  void updateByUserId_success() {
    UUID userId = UUID.randomUUID();
    Instant newTime = Instant.now();
    var req = new UserStatusUpdateRequest(newTime);

    var entity = mock(UserStatus.class);
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(entity));

    var dto = new UserStatusDto(UUID.randomUUID(), userId, newTime);
    given(userStatusMapper.toDto(entity)).willReturn(dto);

    var result = service.updateByUserId(userId, req);

    then(entity).should().update(eq(newTime));
    assertThat(result.userId()).isEqualTo(userId);
    assertThat(result.lastActiveAt()).isEqualTo(newTime);
  }

  @Test
  void updateByUserId_fail_notFound() {
    UUID userId = UUID.randomUUID();
    var req = new UserStatusUpdateRequest(Instant.now());

    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.updateByUserId(userId, req)).isInstanceOf(
        UserStatusNotFoundException.class);
  }

  @Test
  void delete_success() {
    UUID statusId = UUID.randomUUID();
    given(userStatusRepository.existsById(statusId)).willReturn(true);

    service.delete(statusId);

    then(userStatusRepository).should().deleteById(statusId);
  }

  @Test
  void delete_fail_notFound() {
    UUID statusId = UUID.randomUUID();
    given(userStatusRepository.existsById(statusId)).willReturn(false);

    assertThatThrownBy(() -> service.delete(statusId)).isInstanceOf(
        UserStatusNotFoundException.class);
  }
}
