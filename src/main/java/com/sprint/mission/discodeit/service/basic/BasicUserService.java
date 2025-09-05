package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;

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

    // 프로필 메타 생성 후 객체 자체를 보관
    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(req -> {
          BinaryContent bc = new BinaryContent(
              req.fileName(),
              (long) req.bytes().length,
              req.contentType(),
              req.bytes()
          );
          return binaryContentRepository.save(bc);
        })
        .orElse(null);

    String password = userCreateRequest.password();

    User user = new User(username, email, password, nullableProfile);
    User createdUser = userRepository.save(user);

    // UserStatus 는 User 객체 참조
    UserStatus userStatus = new UserStatus(createdUser, Instant.now());
    userStatusRepository.save(userStatus);

    return createdUser;
  }

  @Override
  public UserDto find(UUID userId) {
    return userRepository.findById(userId)
        .map(this::toDto)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
  }

  @Override
  public java.util.List<UserDto> findAll() {
    return userRepository.findAll()
        .stream()
        .map(this::toDto)
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

    // 프로필 교체 요청이 있으면 기존 메타 삭제 후 새 메타 저장 + 교체
    optionalProfileCreateRequest.ifPresent(req -> {
      Optional.ofNullable(user.getProfile())
          .map(BinaryContent::getId)
          .ifPresent(binaryContentRepository::deleteById);

      BinaryContent newMeta = new BinaryContent(
          req.fileName(),
          (long) req.bytes().length,
          req.contentType(),
          req.bytes()
      );
      BinaryContent saved = binaryContentRepository.save(newMeta);
      user.changeProfile(saved);
    });

    String newPassword = userUpdateRequest.newPassword();
    user.update(newUsername, newEmail);     // 이름/이메일 갱신
    user.changePassword(newPassword);       // 비밀번호 갱신

    return userRepository.save(user);
  }

  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    Optional.ofNullable(user.getProfile())
        .map(BinaryContent::getId)
        .ifPresent(binaryContentRepository::deleteById);
    userStatusRepository.deleteByUserId(userId);

    userRepository.deleteById(userId);
  }

  private UserDto toDto(User user) {
    Boolean online = userStatusRepository.findByUserId(user.getId())
        .map(us -> us.getLastActiveAt() != null
            && us.getLastActiveAt().isAfter(Instant.now().minusSeconds(300)))
        .orElse(null);

    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        Optional.ofNullable(user.getProfile())        // profileId 대신 객체에서 id 추출
            .map(BinaryContent::getId)
            .orElse(null),
        online
    );
  }
}
