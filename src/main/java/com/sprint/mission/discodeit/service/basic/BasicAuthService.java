package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService { // 인증 서비스 구현 시작

    private final UserRepository userRepository; // 같은 레이어의 다른 Service가 아닌 Repository만 의존
    private final UserMapper userMapper;

    @Override
    public UserDto login(LoginRequest loginRequest) {
        log.debug("로그인 시도: username={}", loginRequest.username());

        String username = loginRequest.username();
        String password = loginRequest.password();

        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new NoSuchElementException("User with username " + username + " not found"));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Wrong password");
        }

        log.info("로그인 성공: userId={}, username={}", user.getId(), username);
        return userMapper.toDto(user);


    }
}
