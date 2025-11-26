package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionManager;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SessionManager sessionManager;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("updateRoleInternal - 성공: 사용자 권한을 CHANNEL_MANAGER로 변경")
    void updateRoleInternal_ToChannelManager_Success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("testuser", "test@example.com", "encodedPassword", null);
        RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.CHANNEL_MANAGER);

        UserDto expectedDto = new UserDto(
            userId,
            "testuser",
            "test@example.com",
            null,
            true,
            Role.CHANNEL_MANAGER
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        willDoNothing().given(sessionManager).invalidateSessionsByUserId(userId);
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        UserDto result = authService.updateRoleInternal(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo(Role.CHANNEL_MANAGER);

        then(userRepository).should().findById(userId);
        then(sessionManager).should().invalidateSessionsByUserId(userId);
        then(userMapper).should().toDto(user);
    }

    @Test
    @DisplayName("updateRoleInternal - 성공: 사용자 권한을 ADMIN으로 변경")
    void updateRoleInternal_ToAdmin_Success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("testuser", "test@example.com", "encodedPassword", null);
        RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.ADMIN);

        UserDto expectedDto = new UserDto(
            userId,
            "testuser",
            "test@example.com",
            null,
            true,
            Role.ADMIN
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        willDoNothing().given(sessionManager).invalidateSessionsByUserId(userId);
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        UserDto result = authService.updateRoleInternal(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo(Role.ADMIN);

        then(userRepository).should().findById(userId);
        then(sessionManager).should().invalidateSessionsByUserId(userId);
        then(userMapper).should().toDto(user);
    }

    @Test
    @DisplayName("updateRoleInternal - 성공: ADMIN에서 USER로 권한 강등")
    void updateRoleInternal_DemoteToUser_Success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("adminuser", "admin@example.com", "encodedPassword", null);
        user.updateRole(Role.ADMIN);
        RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.USER);

        UserDto expectedDto = new UserDto(
            userId,
            "adminuser",
            "admin@example.com",
            null,
            true,
            Role.USER
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        willDoNothing().given(sessionManager).invalidateSessionsByUserId(userId);
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        UserDto result = authService.updateRoleInternal(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo(Role.USER);

        then(userRepository).should().findById(userId);
        then(sessionManager).should().invalidateSessionsByUserId(userId);
        then(userMapper).should().toDto(user);
    }

    @Test
    @DisplayName("updateRoleInternal - 실패: 존재하지 않는 사용자")
    void updateRoleInternal_UserNotFound_ThrowsException() {
        // given
        UUID userId = UUID.randomUUID();
        RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.CHANNEL_MANAGER);

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.updateRoleInternal(request))
            .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(sessionManager).shouldHaveNoInteractions();
        then(userMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("updateRoleInternal - 성공: 권한 변경 후 세션 무효화 확인")
    void updateRoleInternal_InvalidatesSession() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("testuser", "test@example.com", "encodedPassword", null);
        RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.CHANNEL_MANAGER);

        UserDto expectedDto = new UserDto(
            userId,
            "testuser",
            "test@example.com",
            null,
            false,
            Role.CHANNEL_MANAGER
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        willDoNothing().given(sessionManager).invalidateSessionsByUserId(userId);
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        authService.updateRoleInternal(request);

        // then
        then(sessionManager).should().invalidateSessionsByUserId(userId);
    }
}
