package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import java.time.Instant;
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
class UserStatusServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStatusRepository userStatusRepository;

  @Mock
  private UserStatusMapper userStatusMapper;

  @InjectMocks
  private BasicUserStatusService userStatusService;

  private UUID userId;
  private User user;
  private UserStatus userStatus;
  private UserStatusDto.Detail detail;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();

    user = User.builder()
               .username("tester")
               .email("test@test.com")
               .build();

    userStatus = UserStatus.builder()
                           .user(user)
                           .lastActiveAt(Instant.now())
                           .build();

    detail = UserStatusDto.Detail.builder()
                                 .userId(userId)
                                 .lastActiveAt(userStatus.getLastActiveAt())
                                 .build();
  }

  private void stubCreate() {
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());
    given(userStatusRepository.save(any(UserStatus.class))).willAnswer(
        invocation -> invocation.getArgument(0));
    given(userStatusMapper.toDetail(any(UserStatus.class))).willReturn(detail);
  }

  private void stubUpdateById(UserStatus us) {
    given(userStatusRepository.save(us)).willReturn(us);
    given(userStatusMapper.toDetail(us)).willReturn(detail);
  }

  @Test
  void create_success() {
    stubCreate();

    UserStatusDto.Detail result = userStatusService.create(UserStatusDto.Create.builder()
                                                                               .userId(userId)
                                                                               .build());

    assertThat(result).isEqualTo(detail);
  }

  @Test
  void create_userNotFound() {
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userStatusService.create(UserStatusDto.Create.builder()
                                                                          .userId(userId)
                                                                          .build())).isInstanceOf(
        UserNotFoundException.class);
  }

  @Test
  void create_alreadyExists() {
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));

    assertThatThrownBy(() -> userStatusService.create(UserStatusDto.Create.builder()
                                                                          .userId(userId)
                                                                          .build())).isInstanceOf(
        UserStatusAlreadyExistsException.class);
  }

  @Test
  void find_success() {
    UUID statusId = UUID.randomUUID();

    given(userStatusRepository.findById(statusId)).willReturn(Optional.of(userStatus));
    given(userStatusMapper.toDetail(userStatus)).willReturn(detail);

    UserStatusDto.Detail result = userStatusService.find(statusId);

    assertThat(result).isEqualTo(detail);
  }

  @Test
  void find_notFound() {
    UUID statusId = UUID.randomUUID();
    given(userStatusRepository.findById(statusId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userStatusService.find(statusId)).isInstanceOf(
        UserStatusNotFoundException.class);
  }

  @Test
  void findAll_success() {
    given(userStatusRepository.findAll()).willReturn(List.of(userStatus));
    given(userStatusMapper.toDetail(userStatus)).willReturn(detail);

    List<UserStatusDto.Detail> result = userStatusService.findAll();

    assertThat(result).containsExactly(detail);
  }

  @Test
  void update_success() {
    UUID statusId = UUID.randomUUID();
    given(userStatusRepository.findById(statusId)).willReturn(Optional.of(userStatus));
    stubUpdateById(userStatus);

    UserStatusDto.Detail result = userStatusService.update(statusId);

    assertThat(result).isEqualTo(detail);
  }

  @Test
  void update_notFound() {
    UUID statusId = UUID.randomUUID();
    given(userStatusRepository.findById(statusId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userStatusService.update(statusId)).isInstanceOf(
        UserStatusNotFoundException.class);
  }

  @Test
  void updateByUserId_success() {
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));
    stubUpdateById(userStatus);

    UserStatusDto.Detail result = userStatusService.updateByUserId(userId);

    assertThat(result).isEqualTo(detail);
  }

  @Test
  void updateByUserId_notFound() {
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userStatusService.updateByUserId(userId)).isInstanceOf(
        UserStatusNotFoundException.class);
  }

  @Test
  void delete_success() {
    UUID statusId = UUID.randomUUID();
    given(userStatusRepository.findById(statusId)).willReturn(Optional.of(userStatus));

    userStatusService.delete(statusId);

    verify(userStatusRepository).delete(userStatus);
  }

  @Test
  void delete_notFound() {
    UUID statusId = UUID.randomUUID();
    given(userStatusRepository.findById(statusId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userStatusService.delete(statusId)).isInstanceOf(
        UserStatusNotFoundException.class);
  }

  @Test
  void deleteAll_success() {
    userStatusService.deleteAll();
    verify(userStatusRepository).deleteAll();
  }
}
