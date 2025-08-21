package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.LoginResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public LoginResponse login(LoginRequest loginRequest) {
    User user = userRepository.findByUserName(loginRequest.username()).orElseThrow(()
        -> new IllegalArgumentException("Login: User not found"));
    if (!user.getPassword().equals(loginRequest.password())) {
      throw new IllegalArgumentException("Login: Wrong password");
    }

    byte[] binaryContent = null;
    Optional<BinaryContent> byId = binaryContentRepository.findById(user.getProfileId());
    if (byId.isPresent()) {
      binaryContent = byId.get().getContent();
    }

    return new LoginResponse(
        user.getUserName(),
        user.getEmail(),
        user.getPhoneNumber(),
        binaryContent
    );
  }
}

