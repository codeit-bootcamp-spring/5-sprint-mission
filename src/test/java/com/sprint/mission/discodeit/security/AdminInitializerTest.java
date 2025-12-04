package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.common.config.properties.AdminProperties;
import com.sprint.mission.discodeit.common.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.common.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.common.security.AdminInitializer;
import com.sprint.mission.discodeit.domain.dto.user.data.UserDto;
import com.sprint.mission.discodeit.domain.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.domain.entity.Role;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.repository.UserRepository;
import com.sprint.mission.discodeit.domain.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminInitializer 단위 테스트")
class AdminInitializerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminProperties adminProperties;

    @Mock
    private ApplicationArguments applicationArguments;

    @InjectMocks
    private AdminInitializer adminInitializer;

    @Test
    @DisplayName("run - enabled가 false이면 관리자 계정을 생성하지 않는다")
    void run_DisabledDoesNothing() {
        // given
        given(adminProperties.enabled()).willReturn(false);

        // when
        adminInitializer.run(applicationArguments);

        // then
        then(userService).should(never()).create(any(), any());
        then(userRepository).should(never()).findById(any());
    }

    @Test
    @DisplayName("run - enabled가 true이면 관리자 계정을 생성하고 ADMIN 권한을 부여한다")
    void run_EnabledCreatesAdmin() {
        // given
        UUID adminId = UUID.randomUUID();
        String username = "admin";
        String email = "admin@example.com";
        String password = "adminPassword";

        given(adminProperties.enabled()).willReturn(true);
        given(adminProperties.username()).willReturn(username);
        given(adminProperties.email()).willReturn(email);
        given(adminProperties.password()).willReturn(password);

        UserDto adminDto = new UserDto(adminId, username, email, null, false, Role.USER);
        User adminUser = new User(username, email, "encoded", null);
        given(userService.create(any(UserCreateRequest.class), isNull())).willReturn(adminDto);
        given(userRepository.findById(adminId)).willReturn(Optional.of(adminUser));

        // when
        adminInitializer.run(applicationArguments);

        // then
        then(userService).should().create(any(UserCreateRequest.class), isNull());
        then(userRepository).should().findById(adminId);
        assertThat(adminUser.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("run - 중복된 사용자명이면 예외를 catch하고 정상 종료한다")
    void run_DuplicateUsername_HandlesGracefully() {
        // given
        given(adminProperties.enabled()).willReturn(true);
        given(adminProperties.username()).willReturn("admin");
        given(adminProperties.email()).willReturn("admin@example.com");
        given(adminProperties.password()).willReturn("password");
        given(userService.create(any(UserCreateRequest.class), isNull()))
            .willThrow(new DuplicateUsernameException("admin"));

        // when
        adminInitializer.run(applicationArguments);

        // then
        then(userRepository).should(never()).findById(any());
    }

    @Test
    @DisplayName("run - 중복된 이메일이면 예외를 catch하고 정상 종료한다")
    void run_DuplicateEmail_HandlesGracefully() {
        // given
        given(adminProperties.enabled()).willReturn(true);
        given(adminProperties.username()).willReturn("admin");
        given(adminProperties.email()).willReturn("admin@example.com");
        given(adminProperties.password()).willReturn("password");
        given(userService.create(any(UserCreateRequest.class), isNull()))
            .willThrow(new DuplicateEmailException("admin@example.com"));

        // when
        adminInitializer.run(applicationArguments);

        // then
        then(userRepository).should(never()).findById(any());
    }
}
