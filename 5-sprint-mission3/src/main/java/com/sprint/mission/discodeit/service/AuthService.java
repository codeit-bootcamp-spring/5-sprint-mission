package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service("authService")
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public User login(UserRequest request) {
        List<User> users = userRepository.findAll();

        return users.stream().filter(u -> u.getUsername().equals(request.username()) && u.getPassword().equals(request.password()))
                .findAny().orElseThrow(() -> new NoSuchElementException("일치하는 사용자가 존재하지 않습니다."));
    }
}
