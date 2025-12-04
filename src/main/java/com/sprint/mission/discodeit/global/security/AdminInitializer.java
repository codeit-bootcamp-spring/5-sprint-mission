package com.sprint.mission.discodeit.global.security;

import com.sprint.mission.discodeit.domain.dto.user.data.UserDto;
import com.sprint.mission.discodeit.domain.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.domain.entity.Role;
import com.sprint.mission.discodeit.domain.repository.UserRepository;
import com.sprint.mission.discodeit.domain.service.UserService;
import com.sprint.mission.discodeit.global.config.properties.AdminProperties;
import com.sprint.mission.discodeit.global.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.global.exception.user.DuplicateUsernameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements ApplicationRunner {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AdminProperties adminProperties;

    @Override
    @Transactional
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
            userRepository.findById(admin.id()).ifPresent(user -> user.updateRole(Role.ADMIN));
            log.info("관리자 계정 생성 완료: username={}", admin.username());
        } catch (DuplicateUsernameException | DuplicateEmailException e) {
            log.debug("관리자 계정이 이미 존재합니다: {}", e.getMessage());
        }
    }
}
