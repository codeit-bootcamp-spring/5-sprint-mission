package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    // private final SessionManager sessionManager;
    //
    // @PreAuthorize("hasRole('ADMIN')")
    // @Transactional
    // public UserDto updateRole(RoleUpdateRequest request) {
    //     return updateRoleInternal(request);
    // }
    //
    // @Transactional
    // public UserDto updateRoleInternal(RoleUpdateRequest request) {
    //     UUID userId = request.userId();
    //     User user = userRepository.findById(userId)
    //         .orElseThrow(() -> UserNotFoundException.withId(userId));
    //
    //     Role newRole = request.newRole();
    //     user.updateRole(newRole);
    //
    //     sessionManager.invalidateSessionsByUserId(userId);
    //
    //     return userMapper.toDto(user);
    // }
}
