package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserLoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public User login(UserLoginRequest userLoginRequest) {
        String username = userLoginRequest.username();
        String password = userLoginRequest.password();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Login failed"));

        if (!user.getPassword().equals(password)) {
            throw new NoSuchElementException("Login failed");
        }

        return user;
    }
}
