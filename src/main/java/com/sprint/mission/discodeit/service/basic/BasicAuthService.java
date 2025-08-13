package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.AuthenticationException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUserName(loginRequest.username())
                .orElseThrow(() -> new AuthenticationException("아이디 혹은 비밀번호가 틀렸습니다."));
        if (user.getPassword().equals(loginRequest.password())) {
            throw new AuthenticationException("아이디 혹은 비밀번호가 틀렸습니다.");
        }

        UserStatus status = userStatusRepository.findByUserId(user.getId()).get();
        status.login();

        return new UserResponse(user.getId(), user.getProfileId(), user.getEmail(), user.getUserName(), user.getNickname(), user.getPhoneNumber(), status.isOnline(), status.getLastActiveAt());
    }
}
