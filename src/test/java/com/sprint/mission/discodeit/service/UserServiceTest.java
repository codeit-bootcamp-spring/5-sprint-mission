package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private BinaryContentRepository binaryContentRepository;
  @Mock private UserStatusRepository userStatusRepository;
  @Mock private BinaryContentStorage binaryContentStorage;
  @Mock private UserMapper userMapper;

  @InjectMocks private BasicUserService userService;

  private UUID userId;
  private User user;
  private UserDto userDto;

  private String username;
  private String email;
  private String password;

  private BinaryContent profile;
  private BinaryContentDto profileDto;
  private BinaryContentCreateRequest profileReq;

  @BeforeEach
  void setUp() {
    username = "test";
    email = "test@email.com";
    password = "12341234";

    user = new User(username, email, password, null);
    userId = UUID.randomUUID();
    ReflectionTestUtils.setField(user, "id", userId);

    userDto = new UserDto(userId, username, email, null, false);

    profile = new BinaryContent("profile.jpg", 1L, "image/jpeg");
    ReflectionTestUtils.setField(profile, "id", UUID.randomUUID());
    profileDto = new BinaryContentDto(profile.getId(), "profile.jpg", 1L, "image/jpeg");
    profileReq = new BinaryContentCreateRequest("profile.jpg", "img".getBytes(), "image/jpeg");
  }

  // ========== Create ==========

  @Test
  @DisplayName("사용자 생성 - 프로필 없음(성공)")
  void create_withoutProfile_success() {
    UserCreateRequest req = new UserCreateRequest(username, email, password);

    given(userRepository.existsByUsername(username)).willReturn(false);
    given(userRepository.existsByEmail(email)).willReturn(false);
    given(userRepository.save(any(User.class))).willReturn(user);
    given(userMapper.toDto(any(User.class))).willReturn(userDto);

    UserDto res = userService.create(req, Optional.empty());

    assertThat(res).isEqualTo(userDto);
    verify(userRepository, times(1)).existsByUsername(username);
    verify(userRepository, times(1)).existsByEmail(email);
    verify(userRepository, times(1)).save(any(User.class));
    verify(userMapper, times(1)).toDto(any(User.class));
  }

  @Test
  @DisplayName("사용자 생성 실패 - username 중복")
  void create_usernameConflict() {
    UserCreateRequest req = new UserCreateRequest(username, email, password);
    given(userRepository.existsByUsername(username)).willReturn(true);

    assertThatThrownBy(() -> userService.create(req, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  @Test
  @DisplayName("사용자 생성 실패 - email 중복")
  void create_emailConflict() {
    UserCreateRequest req = new UserCreateRequest(username, email, password);
    given(userRepository.existsByUsername(username)).willReturn(false);
    given(userRepository.existsByEmail(email)).willReturn(true);

    assertThatThrownBy(() -> userService.create(req, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  // ========== Update ==========

  @Test
  @DisplayName("사용자 수정 - 프로필 포함(성공)")
  void update_withProfile_success() {
    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    String newName = "updated";
    String newEmail = "updated@email.com";
    String newPwd = "updated1234";
    UserUpdateRequest req = new UserUpdateRequest(newName, newEmail, newPwd);

    User saved = new User(newName, newEmail, newPwd, profile);
    ReflectionTestUtils.setField(saved, "id", userId);
    UserDto savedDto = new UserDto(userId, newName, newEmail, profileDto, false);

    given(userRepository.existsByUsername(newName)).willReturn(false);
    given(userRepository.existsByEmail(newEmail)).willReturn(false);
    given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(profile);
    given(binaryContentStorage.put(eq(profile.getId()), any())).willReturn(profile.getId());
    given(userRepository.save(any(User.class))).willReturn(saved);
    given(userMapper.toDto(any(User.class))).willReturn(savedDto);

    UserDto res = userService.update(userId, req, Optional.of(profileReq));

    assertThat(res).isEqualTo(savedDto);
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, times(1)).existsByUsername(newName);
    verify(userRepository, times(1)).existsByEmail(newEmail);
    verify(binaryContentRepository, times(1)).save(any(BinaryContent.class));
    verify(binaryContentStorage, times(1)).put(eq(profile.getId()), any());
    verify(userRepository, times(1)).save(any(User.class));
    verify(userMapper, times(1)).toDto(any(User.class));
  }

  @Test
  @DisplayName("사용자 수정 실패 - 존재하지 않는 사용자")
  void update_notFound() {
    UserUpdateRequest req = new UserUpdateRequest("n", "n@email.com", "pass1234");
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.update(userId, req, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);
  }

  // ========== Delete ==========

  @Test
  @DisplayName("사용자 삭제 - 프로필/상태 있음(성공)")
  void delete_success() {
    User withProfile = new User(username, email, password, profile);
    ReflectionTestUtils.setField(withProfile, "id", userId);

    UserStatus status = new UserStatus(withProfile, java.time.Instant.now());
    ReflectionTestUtils.setField(status, "id", UUID.randomUUID());

    given(userRepository.findById(userId)).willReturn(Optional.of(withProfile));
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(status));

    userService.delete(userId);

    verify(userRepository, times(1)).findById(userId);
    verify(binaryContentRepository, times(1)).deleteById(profile.getId());
    verify(userStatusRepository, times(1)).findByUserId(userId);
    verify(userStatusRepository, times(1)).deleteById(status.getId());
    verify(userRepository, times(1)).deleteById(userId);
  }

  @Test
  @DisplayName("사용자 삭제 실패 - 존재하지 않는 사용자")
  void delete_notFound() {
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}
