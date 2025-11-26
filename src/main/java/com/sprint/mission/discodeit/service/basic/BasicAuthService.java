package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public UserResponse updateUserRole(UserRoleUpdateRequest request) {
        log.info("[AuthService] 사용자 권한 수정 시도: {}", request);

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> UserNotFoundException.withId(request.userId()));

        user.updateRole(request.newRole());
        userRepository.save(user);

        sessionManager.invalidateSessionsByUserId(user.getId());

        log.info("[AuthService] 사용자 권한 수정 완료: {} -> {}", user.getUsername(), request.newRole());

        return UserResponse.success(user);
    }
}
