package com.sprint.mission.discodeit.service.jpa;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.AuthResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class JpaAuthService implements AuthService {

  private final UserRepository userRepository;

  @Override
  public AuthResponse login(LoginRequest loginRequest) {
    User user = userRepository.findByUsername(loginRequest.username())
        .orElseThrow(() -> new NoSuchElementException(
            "User with username " + loginRequest.username() + " not found"
        ));

    if (!user.getPassword().equals(loginRequest.password())) {
      throw new IllegalArgumentException("Wrong password");
    }

    return new AuthResponse(user.getId(), user.getUsername());
  }
}
