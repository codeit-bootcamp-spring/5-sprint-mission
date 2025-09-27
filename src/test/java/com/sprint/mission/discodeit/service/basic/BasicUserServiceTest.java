package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
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
class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicUserService userService;

    private UUID userId;
    private String username;
    private String email;
    private String password;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        username = "test";
        email = "test@email.com";
        password = "password1234";

        user = new User(username, email, password, null);
        ReflectionTestUtils.setField(user, "id", userId);
        userDto = new UserDto(userId, username, email, null, true);
    }

    /* CREATE */

    @Test
    @DisplayName("사용자 생성 성공(프로필 이미지 없음)")
    void create_success() {
        UserCreateRequest request = new UserCreateRequest(username, email, password);

        // given
        given(userRepository.existsByEmail(eq(email))).willReturn(false);
        given(userRepository.existsByUsername(eq(username))).willReturn(false);
        given(userMapper.toDto(any(User.class))).willReturn(userDto);

        // when
        UserDto result = userService.create(request, Optional.empty());

        // then
        assertThat(result).isEqualTo(userDto);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 생성 실패(이메일 중복)")
    void create_existsByEmail_DuplicateUserException() {
        UserCreateRequest userCreateRequest = new UserCreateRequest(username, email, password);

        // given
        given(userRepository.existsByEmail(eq(email))).willReturn(true);

        // when then
        assertThatThrownBy(
            () -> userService.create(userCreateRequest, Optional.empty()))
            .isInstanceOf(DuplicateUserException.class);
    }

    /* UPDATE */

    @Test
    @DisplayName("사용자 수정 성공(프로필 이미지 없음)")
    void update_success() {
        String newUsername = "newUsername";
        String newEmail = "new@email.com";
        String newPassword = "newPassword1234";
        UserUpdateRequest request = new UserUpdateRequest(newUsername, newEmail, newPassword);

        // given
        given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(eq(newEmail))).willReturn(false);
        given(userRepository.existsByUsername(eq(newUsername))).willReturn(false);
        given(userMapper.toDto(any(User.class))).willReturn(userDto);

        // when
        UserDto result = userService.update(userId, request, Optional.empty());

        // then
        assertThat(result).isEqualTo(userDto);
    }

    @Test
    @DisplayName("사용자 수정 실패(존재하지 않는 사용자)")
    void update_findById_UserNotFoundException() {
        String newUsername = "newUsername";
        String newEmail = "new@email.com";
        String newPassword = "newPassword1234";
        UserUpdateRequest request = new UserUpdateRequest(newUsername, newEmail, newPassword);

        // given
        given(userRepository.findById(eq(userId))).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(
            () -> userService.update(userId, request, Optional.empty()))
            .isInstanceOf(UserNotFoundException.class);
    }

    /* DELETE */

    @Test
    @DisplayName("사용자 삭제 성공")
    void delete_success() {
        // given
        given(userRepository.existsById(eq(userId))).willReturn(true);

        // when
        userService.delete(userId);

        // then
        verify(userRepository).deleteById(eq(userId));
    }

    @Test
    @DisplayName("사용자 삭제 실패(존재하지 않는 사용자)")
    void delete_existsById_UserNotFoundException() {
        // given
        given(userRepository.existsById(eq(userId))).willReturn(false);

        // when then
        assertThatThrownBy(() -> userService.delete(userId))
            .isInstanceOf(UserNotFoundException.class);
    }
}