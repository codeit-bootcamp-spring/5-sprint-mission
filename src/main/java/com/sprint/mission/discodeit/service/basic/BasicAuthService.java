package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusService userStatusService;

    @Override
    public LoginResponse login(String username, String password) {

        User user = userRepository.findByUsername(username);

        // 비밀번호 확인
        if(!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password");
        }

        // 접속 시간 갱신
        userStatusService.updateLastActive(user);
        boolean isActive = userStatusService.isActive(user.getId());
        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                isActive,
                user.getProfileId()
        );
    }
}
