package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final SessionRegistry sessionRegistry;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public UserDto updateUserRole(UserRoleUpdateRequest request) {
        log.info("[AuthService] 사용자 권한 수정 시도: {}", request);

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> UserNotFoundException.withId(request.userId()));

        user.updateRole(request.role());
        userRepository.save(user);

        List<SessionInformation> sessions = sessionRegistry.getAllSessions(user.getUsername(), false);
        for (SessionInformation session : sessions) {
            session.expireNow();
            log.info("[AuthService] 세션 무효화: sessionId={}", session.getSessionId());
        }

        log.info("[AuthService] 사용자 권한 수정 완료: {} -> {}", user.getUsername(), request.role());

        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getProfile() != null ? user.getProfile().getId() : null,
                null
        );
    }
}
