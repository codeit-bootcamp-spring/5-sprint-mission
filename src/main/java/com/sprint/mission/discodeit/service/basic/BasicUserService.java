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
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final BinaryContentStorage binaryContentStorage; // ⬅ 바이너리 저장소

  @Override
  public User create(
      UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest
  ) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("User with email " + email + " already exists");
    }
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("User with username " + username + " already exists");
    }

    // 프로필 메타 저장 + 바이트는 Storage에 저장
    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(req -> {
          var meta = new BinaryContent(req.fileName(), (long) req.bytes().length, req.contentType());
          try {
            binaryContentStorage.put(meta.getId(), req.bytes());
          } catch (IOException e) {
            throw new UncheckedIOException("Failed to store profile binary: " + meta.getId(), e);
          }
          return binaryContentRepository.save(meta);
        })
        .orElse(null);

    User createdUser = userRepository.save(
        new User(username, email, userCreateRequest.password(), nullableProfile)
    );

    userStatusRepository.save(new UserStatus(createdUser, Instant.now()));
    return createdUser;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID userId) {
    return userRepository.findById(userId)
        .map(user -> {
          Boolean online = userStatusRepository.findByUser_Id(user.getId())
              .map(us -> us.getLastActiveAt() != null
                  && us.getLastActiveAt().isAfter(Instant.now().minusSeconds(300)))
              .orElse(null);
          return userMapper.toDto(user, online);
        })
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public java.util.List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(user -> {
          Boolean online = userStatusRepository.findByUser_Id(user.getId())
              .map(us -> us.getLastActiveAt() != null
                  && us.getLastActiveAt().isAfter(Instant.now().minusSeconds(300)))
              .orElse(null);
          return userMapper.toDto(user, online);
        })
        .toList();
  }

  @Override
  public User update(
      UUID userId,
      UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest
  ) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    if (userRepository.existsByEmail(newEmail)) {
      throw new IllegalArgumentException("User with email " + newEmail + " already exists");
    }
    if (userRepository.existsByUsername(newUsername)) {
      throw new IllegalArgumentException("User with username " + newUsername + " already exists");
    }

    // 프로필 교체: 메타 새로 저장 + Storage에 바이트 저장
    optionalProfileCreateRequest.ifPresent(req -> {
      Optional.ofNullable(user.getProfile())
          .ifPresent(old -> binaryContentRepository.deleteById(old.getId()));

      var meta = new BinaryContent(req.fileName(), (long) req.bytes().length, req.contentType());
      try {
        binaryContentStorage.put(meta.getId(), req.bytes());
      } catch (IOException e) {
        throw new UncheckedIOException("Failed to store profile binary: " + meta.getId(), e);
      }
      var saved = binaryContentRepository.save(meta);
      user.changeProfile(saved);
    });

    user.update(newUsername, newEmail);
    user.changePassword(userUpdateRequest.newPassword());
    return userRepository.save(user);
  }

  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    Optional.ofNullable(user.getProfile())
        .ifPresent(old -> binaryContentRepository.deleteById(old.getId()));
    userStatusRepository.deleteByUser_Id(userId);

    userRepository.deleteById(userId);
  }
}
