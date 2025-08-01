package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    UserRepository userRepository;

    @Override
    public User login(String username, String password) {

        User user = userRepository.findByName(username).orElse(null);

        if(user == null || user.getPassword() == null || !user.getPassword().equals(password) ) {
            throw new IllegalArgumentException("로그인 실패");
        }

        return user;
    }
}
