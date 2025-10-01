// src/test/java/com/sprint/mission/discodeit/service/UserServiceTest.java
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserStatusRepository userStatusRepository;
  @Mock
  private UserMapper userMapper;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentStorage binaryContentStorage;

  @InjectMocks
  private BasicUserService userService;

  @Test
  void create_success_withoutProfile() {
    // given
    var req = new UserCreateRequest("neo", "neo@matrix.io", "secret123");
    given(userRepository.existsByEmail("neo@matrix.io")).willReturn(false);
    given(userRepository.existsByUsername("neo")).willReturn(false);

    given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

    var expectedId = UUID.randomUUID();
    var expectedDto = new UserDto(expectedId, "neo", "neo@matrix.io", null, true);
    given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

    var dto = userService.create(req, Optional.empty());

    assertThat(dto).isEqualTo(expectedDto);
    then(binaryContentRepository).should(never()).save(any(BinaryContent.class));
    then(binaryContentStorage).should(never()).put(any(), any());
    then(userRepository).should().save(any(User.class));
  }

  @Test
  void create_success_withProfile() {
    var req = new UserCreateRequest("trinity", "tri@matrix.io", "secret123");
    given(userRepository.existsByEmail("tri@matrix.io")).willReturn(false);
    given(userRepository.existsByUsername("trinity")).willReturn(false);

    byte[] bytes = "hello".getBytes();
    var profileReq = new BinaryContentCreateRequest("p.png", "image/png", bytes);

    given(binaryContentRepository.save(any(BinaryContent.class))).willAnswer(
        inv -> inv.getArgument(0));

    willDoNothing().given(binaryContentStorage).put(any(), eq(bytes));

    var expectedId = UUID.randomUUID();
    var expectedDto = new UserDto(expectedId, "trinity", "tri@matrix.io", null, false);
    given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

    var dto = userService.create(req, Optional.of(profileReq));

    assertThat(dto.username()).isEqualTo("trinity");
    then(binaryContentRepository).should().save(any(BinaryContent.class));
    then(binaryContentStorage).should().put(any(), eq(bytes));
    then(userRepository).should().save(any(User.class));
  }

  @Test
  void create_fail_emailExists() {
    var req = new UserCreateRequest("neo", "neo@matrix.io", "secret123");
    given(userRepository.existsByEmail("neo@matrix.io")).willReturn(true);

    assertThatThrownBy(() -> userService.create(req, Optional.empty())).isInstanceOf(
        UserAlreadyExistsException.class);
  }

  @Test
  void create_fail_usernameExists() {
    var req = new UserCreateRequest("neo", "neo@matrix.io", "secret123");
    given(userRepository.existsByEmail("neo@matrix.io")).willReturn(false);
    given(userRepository.existsByUsername("neo")).willReturn(true);

    assertThatThrownBy(() -> userService.create(req, Optional.empty())).isInstanceOf(
        UserAlreadyExistsException.class);
  }

  @Test
  void find_success() {
    var user = mock(User.class);
    UUID id = UUID.randomUUID();
    given(userRepository.findById(id)).willReturn(Optional.of(user));

    var expectedDto = new UserDto(id, "neo", "neo@matrix.io", null, true);
    given(userMapper.toDto(user)).willReturn(expectedDto);

    var dto = userService.find(id);

    assertThat(dto).isEqualTo(expectedDto);
  }

  @Test
  void find_fail_notFound() {
    UUID id = UUID.randomUUID();
    given(userRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.find(id)).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void update_success_withProfile() {
    UUID id = UUID.randomUUID();
    var found = mock(User.class);
    given(userRepository.findById(id)).willReturn(Optional.of(found));

    var upd = new UserUpdateRequest("newName", "new@mail.io", "newpass");
    byte[] bytes = "img".getBytes();
    var profileReq = new BinaryContentCreateRequest("p.png", "image/png", bytes);

    given(binaryContentRepository.save(any(BinaryContent.class))).willAnswer(
        inv -> inv.getArgument(0));
    willDoNothing().given(binaryContentStorage).put(any(), eq(bytes));

    var expectedDto = new UserDto(id, "newName", "new@mail.io", null, false);
    given(userMapper.toDto(found)).willReturn(expectedDto);

    var dto = userService.update(id, upd, Optional.of(profileReq));

    then(found).should().update(eq("newName"), eq("new@mail.io"), eq("newpass"), any());
    assertThat(dto).isEqualTo(expectedDto);
  }

  @Test
  void update_fail_userNotFound() {
    UUID id = UUID.randomUUID();
    given(userRepository.findById(id)).willReturn(Optional.empty());

    var upd = new UserUpdateRequest("a", "b@mail.io", "p");

    assertThatThrownBy(() -> userService.update(id, upd, Optional.empty())).isInstanceOf(
        UserNotFoundException.class);
  }

  @Test
  void delete_success() {
    UUID id = UUID.randomUUID();
    given(userRepository.existsById(id)).willReturn(true);

    userService.delete(id);

    then(userRepository).should().deleteById(id);
  }

  @Test
  void delete_fail_notFound() {
    UUID id = UUID.randomUUID();
    given(userRepository.existsById(id)).willReturn(false);

    assertThatThrownBy(() -> userService.delete(id)).isInstanceOf(UserNotFoundException.class);
  }
}
