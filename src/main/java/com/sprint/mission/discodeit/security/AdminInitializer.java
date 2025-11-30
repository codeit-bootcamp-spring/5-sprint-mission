package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.config.properties.AdminProperties;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
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
            User user = userRepository.findById(admin.id()).orElseThrow();
            user.updateRole(Role.ADMIN);
            log.info("관리자 계정 생성 완료: username={}", admin.username());
        } catch (DuplicateUsernameException | DuplicateEmailException e) {
            log.debug("관리자 계정이 이미 존재합니다: {}", e.getMessage());
        }
    }
}
