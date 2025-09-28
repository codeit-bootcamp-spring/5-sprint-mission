package com.sprint.mission.discodeit.service.basic;

<<<<<<< HEAD
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.time.Instant;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponseDto login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. " + loginRequest.username()));

        if (!user.getPassword().equals(loginRequest.password())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        // 로그인 시 현재 시각으로 상태 갱신
        UserStatus status = userStatusRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserStatus newStatus = new UserStatus(user.getId(), Instant.now());
                    userStatusRepository.save(newStatus);
                    return newStatus;
                });
        status.update(Instant.now()); // 로그인 시 lastActiveAt 갱신
        userStatusRepository.save(status);

        return UserResponseDto.fromEntity(user, status);
    }
=======
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.InvalidCredentialsException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional(readOnly = true)
  @Override
  public UserDto login(LoginRequest loginRequest) {
    String username = loginRequest.username();
    String password = loginRequest.password();

    log.info("[AUTH][LOGIN] username={}", username);

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          log.warn("[AUTH][LOGIN][FAILED] username={} not found", username);
          return new InvalidCredentialsException(username);
        });

    if (!user.getPassword().equals(password)) {
      log.warn("[AUTH][LOGIN][FAILED] username={} wrong password", username);
      throw new InvalidCredentialsException(username);
    }

    UserDto userDto = userMapper.toDto(user);
    log.debug("[AUTH][LOGIN][DONE] userId={}", userDto.id());
    return userDto;
  }
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
}
