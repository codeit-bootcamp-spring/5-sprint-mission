package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AuthServiceRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("basicAuthService")
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    @Override
    public User login(AuthServiceRequest authServiceRequest) {
        String username = authServiceRequest.username();
        String password = authServiceRequest.password();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User with username " + username + " not found"));

        if(!user.getPassword().equals(password)){
            throw new IllegalArgumentException("Invalid password");
        }
        return user;
    }
}
