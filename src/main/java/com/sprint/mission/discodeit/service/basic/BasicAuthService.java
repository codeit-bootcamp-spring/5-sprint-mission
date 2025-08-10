package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service("BasicAuthService")
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public User login(LoginRequest request) {
        return userRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(request.getUsername()) &&
                        user.getPassword().equals(request.getPassword()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Invalid username or password"));
    }
}
