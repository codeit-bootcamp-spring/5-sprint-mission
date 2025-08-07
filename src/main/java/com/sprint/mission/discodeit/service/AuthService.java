package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserLoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public User login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByUsername(userLoginRequest.username())
                .orElseThrow(() -> new NoSuchElementException("login : 아이디 또는 비밀번호가 잘못되었습니다"));
        if (!user.getPassword().equals(userLoginRequest.password())) {
            throw new IllegalArgumentException("login : 아이디 또는 비밀번호가 잘못되었습니다");
        }
        return user;
    }
}
