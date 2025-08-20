package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileAuthService implements AuthService {

    private final UserRepository userRepository;

    @Override
    public User login(LoginRequest loginRequest) {
        if (loginRequest.getUserId() == null || loginRequest.getPassword() == null) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 누락되었습니다.");
        }
        return userRepository.findByUserIdAndPassword(
                loginRequest.getUserId(),
                loginRequest.getPassword()
        ).orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
    }
}

