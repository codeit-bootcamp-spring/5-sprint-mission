package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.UnauthorizedException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    @Transactional
    public UserDto login(LoginRequest request) {
        String username = request.username().strip().toLowerCase(Locale.ROOT);

        log.debug("로그인 시도: username={}", username);

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UnauthorizedException("사용자 이름 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("사용자 이름 또는 비밀번호가 올바르지 않습니다.");
        }

        user.getUserStatus().update(Instant.now());

        log.info("로그인 성공: userId={}, username={}", user.getId(), user.getUsername());

        return userMapper.toDto(user);
    }
}
