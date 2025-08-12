package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.response.AuthLoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("authService")
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    // username, password과 일치하는 유저가 있는지 확인합니다.
    // 일치하는 유저가 있는 경우: 유저 정보 반환
    // 일치하는 유저가 없는 경우: 예외 발생

    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {

        Optional<User> optionalUser = userRepository.findById(request.userId());

        // 유저 없으면 -> 예외 발생
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = optionalUser.get();

        if(!user.getUsername().equals(request.username()) ||
                !user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("Wrong username or password");
        }
        return new AuthLoginResponse(user);
    }
}
