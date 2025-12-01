package com.sprint.mission.discodeit.service.init;


import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class InitService {
    @Value("${discodeit.admin.username}")
    private String adminUsername;
    @Value("${discodeit.admin.password}")
    private String adminPassword;
    @Value("${discodeit.admin.email}")
    private String adminEmail;
    private final UserService userService;
    private final AuthService authService;

    @Value("${discodeit.user.username}")
    private String userUsername;
    @Value("${discodeit.user.password}")
    private String userPassword;
    @Value("${discodeit.user.email}")
    private String userEmail;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void initAdmin() {
        // 관리자 계정 초기화 로직
        UserCreateRequest request = new UserCreateRequest(adminUsername, adminEmail, adminPassword);
        try {
            UserDto admin = userService.create(request, Optional.empty());
            authService.updateRoleInternal(new RoleUpdateRequest(admin.id(), Role.ADMIN));
            log.info("관리자 계정이 성공적으로 생성되었습니다.");
        } catch (UserAlreadyExistsException e) {
            log.warn("관리자 계정이 이미 존재합니다");
        } catch (Exception e) {
            log.error("관리자 계정 생성 중 오류가 발생했습니다.: {}", e.getMessage());
        }
    }

    @Transactional
    public void initDefaultUser() {
        // 관리자 계정 초기화 로직
        UserCreateRequest request = new UserCreateRequest(userUsername, userEmail, userPassword);
        try {
            UserDto user = userService.create(request, Optional.empty());
            authService.updateRoleInternal(new RoleUpdateRequest(user.id(), Role.USER));
            log.info("사용지 계정이 성공적으로 생성되었습니다.");
        } catch (UserAlreadyExistsException e) {
            log.warn("사용지 계정이 이미 존재합니다");
        } catch (Exception e) {
            log.error("사용자 계정 생성 중 오류가 발생했습니다.: {}", e.getMessage());
        }
    }


}
