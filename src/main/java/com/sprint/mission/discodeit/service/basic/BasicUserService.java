package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.*;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final BinaryContentStorage binaryContentStorage;

  // CREATE → UserDto 반환
  @Override
  public UserDto create(UserCreateRequest req,
      Optional<BinaryContentCreateRequest> optionalProfile) {

    String username = req.username();
    String email = req.email();

    // 중복 체크는 명확히 409로
    if (userRepository.existsByEmail(email)) {
      throw new ResponseStatusException(CONFLICT, "email already exists");
    }
    if (userRepository.existsByUsername(username)) {
      throw new ResponseStatusException(CONFLICT, "username already exists");
    }

    BinaryContent profile = optionalProfile
        .map(c -> {
          BinaryContent meta = new BinaryContent(c.fileName(), (long) c.bytes().length, c.contentType());
          // 1) 메타 저장 → id 생성
          BinaryContent saved = binaryContentRepository.save(meta);
          // 2) 스토리지 저장
          try {
            binaryContentStorage.put(saved.getId(), c.bytes());
          } catch (IOException e) {
            throw new UncheckedIOException("Failed to store profile binary: " + saved.getId(), e);
          }
          return saved;
        })
        .orElse(null);

    User user = userRepository.save(new User(username, email, req.password(), profile));
    userStatusRepository.save(new UserStatus(user, Instant.now()));
    // 가입 직후 online은 false
    return userMapper.toDto(user, false);
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto find(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User with id " + userId + " not found"));

    Boolean online = userStatusRepository.findByUser_Id(user.getId())
        .map(us -> us.getLastActiveAt() != null
            && us.getLastActiveAt().isAfter(Instant.now().minusSeconds(300)))
        .orElse(null);

    return userMapper.toDto(user, online);
  }

  @Transactional(readOnly = true)
  @Override
  public List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(u -> {
          Boolean online = userStatusRepository.findByUser_Id(u.getId())
              .map(us -> us.getLastActiveAt() != null
                  && us.getLastActiveAt().isAfter(Instant.now().minusSeconds(300)))
              .orElse(null);
          return userMapper.toDto(u, online);
        })
        .toList();
  }

  // UPDATE → UserDto 반환
  @Override
  public UserDto update(UUID userId,
      UserUpdateRequest req,
      Optional<BinaryContentCreateRequest> optionalProfile) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User with id " + userId + " not found"));

    String newUsername = req.newUsername();
    String newEmail = req.newEmail();

    // 바뀌는 경우에만 중복 체크
    if (newEmail != null && !newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
      throw new ResponseStatusException(CONFLICT, "email already exists");
    }
    if (newUsername != null && !newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
      throw new ResponseStatusException(CONFLICT, "username already exists");
    }

    // 프로필 교체
    optionalProfile.ifPresent(c -> {
      Optional.ofNullable(user.getProfile()).ifPresent(old -> binaryContentRepository.deleteById(old.getId()));

      BinaryContent meta = new BinaryContent(c.fileName(), (long) c.bytes().length, c.contentType());
      BinaryContent saved = binaryContentRepository.save(meta);        // 먼저 save 해서 id 생성
      try {
        binaryContentStorage.put(saved.getId(), c.bytes());
      } catch (IOException e) {
        throw new UncheckedIOException("Failed to store profile binary: " + saved.getId(), e);
      }
      user.changeProfile(saved);
    });
    // 부분 업데이트(널이면 유지)
    if (newUsername != null) user.update(newUsername, user.getEmail());
    if (newEmail != null)    user.update(user.getUsername(), newEmail);
    if (req.newPassword() != null) user.changePassword(req.newPassword());
    return userMapper.toDto(user);
  }

  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User with id " + userId + " not found"));

    Optional.ofNullable(user.getProfile())
        .ifPresent(old -> binaryContentRepository.deleteById(old.getId()));

    userStatusRepository.deleteByUser_Id(userId);
    userRepository.deleteById(userId);
  }
}
