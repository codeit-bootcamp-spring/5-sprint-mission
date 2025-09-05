package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
  public User create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("User with email " + email + " already exists");
    }
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("User with username " + username + " already exists");
    }

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

    // UserStatus 는 User 참조 보유
    UserStatus userStatus = new UserStatus(createdUser, Instant.now());
    userStatusRepository.save(userStatus);

    return createdUser;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID userId) {
    return userRepository.findById(userId)
        .map(this::toDto)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public java.util.List<UserDto> findAll() {
    return userRepository.findAll()
        .stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  public User update(UUID userId,
      UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    // 자기 자신 제외 중복 체크
    if (!Objects.equals(user.getEmail(), newEmail) && userRepository.existsByEmail(newEmail)) {
      throw new IllegalArgumentException("User with email " + newEmail + " already exists");
    }
    if (!Objects.equals(user.getUsername(), newUsername) && userRepository.existsByUsername(
        newUsername)) {
      throw new IllegalArgumentException("User with username " + newUsername + " already exists");
    }

    // 프로필 교체: 참조 끊고 삭제 → 새로 저장 → 교체
    optionalProfileCreateRequest.ifPresent(req -> {
      BinaryContent old = user.getProfile();
      if (old != null) {
        user.changeProfile(null);
        userRepository.save(user); // FK 끊기
        binaryContentRepository.deleteById(old.getId());
      }

      BinaryContent saved = binaryContentRepository.save(
          new BinaryContent(req.fileName(), (long) req.bytes().length, req.contentType(),
              req.bytes())
      );
      user.changeProfile(saved);
    });

    String newPassword = userUpdateRequest.newPassword();
    user.update(newUsername, newEmail);
    if (newPassword != null && !newPassword.isBlank()) {
      user.changePassword(newPassword);
    }

    return userRepository.save(user);
  }

  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    // FK 끊은 다음 첨부 메타 삭제
    BinaryContent profile = user.getProfile();
    if (profile != null) {
      user.changeProfile(null);
      userRepository.save(user); // FK 해제
      binaryContentRepository.deleteById(profile.getId());
    }

    // UserStatus 정리 (경로 기반 메서드)
    userStatusRepository.deleteByUser_Id(userId);

    userRepository.delete(user);
  }

  private UserDto toDto(User user) {
    Boolean online = userStatusRepository.findByUser_Id(user.getId())
        .map(us -> {
          Instant last = us.getLastActiveAt();
          return last != null && last.isAfter(Instant.now().minusSeconds(300));
        })
        .orElse(null);

    // ✅ BinaryContent → BinaryContentDto 매핑
    BinaryContentDto profileDto = Optional.ofNullable(user.getProfile())
        .map(p -> new BinaryContentDto(
            p.getId(),
            p.getFileName(),
            p.getSize(),
            p.getContentType(),
            p.getBytes()
        ))
        .orElse(null);

    // ✅ UserDto(id, username, email, BinaryContentDto profile, Boolean online)
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        profileDto,
        online
    );
  }
}