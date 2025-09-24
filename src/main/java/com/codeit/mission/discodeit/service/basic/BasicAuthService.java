package com.codeit.mission.discodeit.service.basic;

import com.codeit.mission.discodeit.dto.data.UserDto;
import com.codeit.mission.discodeit.dto.request.LoginRequest;
import com.codeit.mission.discodeit.entity.User;
import com.codeit.mission.discodeit.mapper.UserMapper;
import com.codeit.mission.discodeit.repository.UserRepository;
import com.codeit.mission.discodeit.service.AuthService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public UserDto login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "User with username " + username + " not found"));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Wrong password");
        }

        return userMapper.toDto(user);
    }
}
