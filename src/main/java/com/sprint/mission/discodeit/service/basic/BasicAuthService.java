package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserLoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service("authService")
@RequiredArgsConstructor
@Validated
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public User login(@Valid UserLoginRequest userLoginRequest) {
        User user = userRepository.findByUsername(userLoginRequest.username())
                .orElseThrow(() -> new SecurityException("login : 아이디 또는 비밀번호가 잘못되었습니다"));
        if (!user.getPassword().equals(userLoginRequest.password())) {
            throw new SecurityException("login : 아이디 또는 비밀번호가 잘못되었습니다");
        }
        return user;
    }
}
