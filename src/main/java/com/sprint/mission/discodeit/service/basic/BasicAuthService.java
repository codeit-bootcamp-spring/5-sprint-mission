package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;

import java.util.NoSuchElementException;

public class BasicAuthService implements AuthService {
    private UserRepository userRepository;

    @Override
    public User login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException(username + "를 찾을 수 없습니다"));

        if (!user.getPassword().equals(password)) {
            throw new NoSuchElementException("비밀번호가 틀렸습니다");
        }
        return user;
    }
}
