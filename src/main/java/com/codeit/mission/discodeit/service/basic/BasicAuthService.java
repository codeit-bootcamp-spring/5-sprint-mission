package com.codeit.mission.discodeit.service.basic;

import com.codeit.mission.discodeit.dto.auth.LoginRequest;
import com.codeit.mission.discodeit.dto.auth.LoginResponse;
import com.codeit.mission.discodeit.entity.User;
import com.codeit.mission.discodeit.repository.UserRepository;
import com.codeit.mission.discodeit.service.AuthService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service("basicAuthService")
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    public BasicAuthService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        User matchedUser = findUser(request.getUsername(), request.getPassword());

        if (matchedUser == null) {
            throw new NoSuchElementException("User not found");
        }

        return new LoginResponse(matchedUser);
    }

    private User findUser(String username, String password) {
        List<User> users = userRepository.findAll();

        return users.stream()
                .filter(user -> username.equals(user.getUsername()) && password.equals(user.getPassword()))
                .findFirst()
                .orElse(null);
    }
}
