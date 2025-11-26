package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionManager sessionManager;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateRole(RoleUpdateRequest request) {
        return updateRoleInternal(request);
    }

    @Transactional
    public UserDto updateRoleInternal(RoleUpdateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);

        Role newRole = request.newRole();
        user.updateRole(newRole);

        sessionManager.invalidateSessionsByUserId(userId);

        return userMapper.toDto(user);
    }
}
