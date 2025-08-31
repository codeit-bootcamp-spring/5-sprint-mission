package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UnauthorizedException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    @Transactional
    public UserDto login(LoginRequest req) {
        String username = req.username().strip().toLowerCase(Locale.ROOT);

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UnauthorizedException("Username or password incorrect"));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new UnauthorizedException("Username or password incorrect");
        }

        UserStatus userStatus = userStatusRepository.getOrCreateByUser(user);
        userStatus.setLastActiveAt(Instant.now());
        user.setUserStatus(userStatus);

        return userMapper.toDto(user);
    }
}
