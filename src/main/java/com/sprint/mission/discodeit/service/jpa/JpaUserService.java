package com.sprint.mission.discodeit.service.jpa;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class JpaUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  public User create(UserCreateRequest request, Optional<BinaryContentCreateRequest> optionalProfile) {
    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("User with email " + request.email() + " already exists");
    }
    if (userRepository.existsByUsername(request.username())) {
      throw new IllegalArgumentException("User with username " + request.username() + " already exists");
    }

    BinaryContent profile = optionalProfile
        .map(req -> binaryContentRepository.save(new BinaryContent(
            req.fileName(), (long) req.bytes().length, req.contentType())))
        .orElse(null);

    User user = new User(request.username(), request.email(), request.password(), profile, false);
    User savedUser = userRepository.save(user);

    UserStatus status = new UserStatus(savedUser, Instant.now());
    savedUser.setStatus(status);
    userStatusRepository.save(status);

    return savedUser;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
    Boolean online = Optional.ofNullable(user.getStatus()).map(UserStatus::isOnline).orElse(null);
    return toDto(user, online);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(u -> toDto(u, Optional.ofNullable(u.getStatus()).map(UserStatus::isOnline).orElse(null)))
        .toList();
  }

  @Override
  public User update(UUID userId, UserUpdateRequest request, Optional<BinaryContentCreateRequest> optionalProfile) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

    if (request.newUsername() != null && !request.newUsername().equals(user.getUsername())
        && userRepository.existsByUsername(request.newUsername())) {
      throw new IllegalArgumentException("User with username " + request.newUsername() + " already exists");
    }
    if (request.newEmail() != null && !request.newEmail().equals(user.getEmail())
        && userRepository.existsByEmail(request.newEmail())) {
      throw new IllegalArgumentException("User with email " + request.newEmail() + " already exists");
    }

    BinaryContent newProfile = optionalProfile.map(req -> {
      if (user.getProfile() != null) binaryContentRepository.delete(user.getProfile());
      return binaryContentRepository.save(new BinaryContent(
          req.fileName(), (long) req.bytes().length, req.contentType()));
    }).orElse(null);

    user.update(request.newUsername(), request.newEmail(), request.newPassword(), newProfile);
    return user; // Dirty Checking으로 자동 반영
  }

  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

    if (user.getProfile() != null) binaryContentRepository.delete(user.getProfile());
    if (user.getStatus() != null) userStatusRepository.delete(user.getStatus());
    userRepository.delete(user);
  }

  private UserDto toDto(User user, Boolean online) {
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getProfile() != null ? binaryContentMapper.toDto(user.getProfile()) : null,
        online
    );
  }
}
