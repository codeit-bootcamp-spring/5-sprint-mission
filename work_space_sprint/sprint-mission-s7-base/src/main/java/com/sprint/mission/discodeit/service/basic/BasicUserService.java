package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
      log.info("사용자 생성 시작: username={}, email={}", userCreateRequest.username(), userCreateRequest.email());
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
        log.warn("사용자 생성 실패 - 이메일 중복: email={}", email);
      throw new UserAlreadyExistsException("email", email);
    }
    if (userRepository.existsByUsername(username)) {
        log.warn("사용자 생성 실패 - 사용자명 중복: username={}", username);
        throw new UserAlreadyExistsException("username", username);
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;
        })
        .orElse(null);
    String password = userCreateRequest.password();

    User user = new User(username, email, password, nullableProfile);
    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(user, now);

    userRepository.save(user);
    log.info("사용자 생성 완료: userId={}, username={}, email={}", user.getId(), user.getUsername(), user.getEmail());
    return userMapper.toDto(user);
  }

  @Override
  public UserDto find(UUID userId) {
    return userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  @Override
  public List<UserDto> findAll() {
    return userRepository.findAllWithProfileAndStatus()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
      log.info("사용자 수정 시작: userId={}, newUsername={}", userId, userUpdateRequest.newUsername());
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (userRepository.existsByEmail(newEmail)) {
        log.warn("사용자 수정 실패 - 이메일 중복: email={}", newEmail);
        throw new UserAlreadyExistsException("email", newEmail);
    }
    if (userRepository.existsByUsername(newUsername)) {
        log.warn("사용자 수정 실패 - 사용자명 중복: username={}", newUsername);
        throw new UserAlreadyExistsException("username", newUsername);
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {

          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;
        })
        .orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    user.update(newUsername, newEmail, newPassword, nullableProfile);
        log.info("사용자 수정 완료: userId={}, newUsername={}, email={}", userId, newUsername, newEmail);
    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  public void delete(UUID userId) {
      log.info("사용자 삭제 시작: userId={}", userId);
    if (!userRepository.existsById(userId)) {
        log.warn("사용자 삭제 실패 - 존재하지 않는 아이디: userId={}", userId);
        throw new UserNotFoundException(userId);
    }

    userRepository.deleteById(userId);
    log.info("사용자 삭제 완료: userId={}", userId);
  }
}
