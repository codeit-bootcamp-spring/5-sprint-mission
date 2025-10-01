package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UsernameAlreadyExistsException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserStatusRepository userStatusRepository;
    @Mock UserMapper userMapper;
    @Mock BinaryContentRepository binaryContentRepository;
    @Mock BinaryContentStorage binaryContentStorage;

    @InjectMocks BasicUserService userService;

    @Test
    @DisplayName("유저 생성 성공")
    void createUserSuccess() {
        // given
        UserCreateRequest request = new UserCreateRequest("mike", "mike@test.com", "password1234");
        User user = new User("mike", "mike@test.com", "password1234", null);
        UserDto dto = new UserDto(UUID.randomUUID(), "mike", "mike@test.com", null, false);

        given(userRepository.existsByEmail("mike@test.com")).willReturn(false);
        given(userRepository.existsByUsername("mike")).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(userMapper.toDto(any(User.class))).willReturn(dto);

        // when
        UserDto result = userService.create(request, Optional.empty());

        // then
        assertThat(result.username()).isEqualTo("mike");
        then(userRepository).should(times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("유저 생성 실패 - 이메일 중복")
    void createUserFailByEmail() {
        // given
        UserCreateRequest request = new UserCreateRequest("mike", "mike@test.com", "password1234");
        given(userRepository.existsByEmail("mike@test.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    @DisplayName("유저 생성 실패 - 사용자명 중복")
    void createUserFailByUsername() {
        // given
        UserCreateRequest request = new UserCreateRequest("mike", "mike@test.com", "password1234");
        given(userRepository.existsByEmail("mike@test.com")).willReturn(false);
        given(userRepository.existsByUsername("mike")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
                .isInstanceOf(UsernameAlreadyExistsException.class);
    }

    @Test
    @DisplayName("유저 수정 성공")
    void updateUserSuccess() {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("newName", "new@test.com", "newPass1234");
        User user = new User("oldName", "old@test.com", "oldPass", null);
        UserDto dto = new UserDto(userId, "newName", "new@test.com", null, false);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("new@test.com")).willReturn(false);
        given(userRepository.existsByUsername("newName")).willReturn(false);
        given(userMapper.toDto(any(User.class))).willReturn(dto);

        // when
        UserDto result = userService.update(userId, request, Optional.empty());

        // then
        assertThat(result.email()).isEqualTo("new@test.com");
        assertThat(result.username()).isEqualTo("newName");
    }

    @Test
    @DisplayName("유저 수정 실패 - 존재하지 않는 유저")
    void updateUserFailByNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("newName", "new@test.com", "newPass1234");

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("유저 삭제 성공")
    void deleteUserSuccess() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.existsById(userId)).willReturn(true);

        // when
        userService.delete(userId);

        // then
        then(userRepository).should().deleteById(userId);
    }

    @Test
    @DisplayName("유저 삭제 실패 - 존재하지 않는 유저")
    void deleteUserFailByNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.existsById(userId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.delete(userId))
                .isInstanceOf(UserNotFoundException.class);
    }
}