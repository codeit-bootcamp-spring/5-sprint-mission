package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.config.properties.AdminProperties;
import com.sprint.mission.discodeit.dto.auth.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements ApplicationRunner {

    private final UserService userService;
    private final AuthService authService;
    private final AdminProperties adminProperties;

    @Override
    public void run(ApplicationArguments args) {
        if (!adminProperties.enabled()) {
            return;
        }

        UserCreateRequest request = new UserCreateRequest(
            adminProperties.username(),
            adminProperties.email(),
            adminProperties.password()
        );

        try {
            UserDto admin = userService.create(request, null);
            authService.updateRoleWithoutAuth(new RoleUpdateRequest(admin.id(), Role.ADMIN));
            log.info("관리자 계정 생성 완료: username={}", admin.username());
        } catch (DuplicateUsernameException | DuplicateEmailException e) {
            log.debug("관리자 계정이 이미 존재합니다: {}", e.getMessage());
        }
    }
}
