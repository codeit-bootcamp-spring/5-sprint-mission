package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.neutral.UserCommand;
import com.sprint.mission.discodeit.dto.response.UserFindResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service("userService")
@RequiredArgsConstructor
@Validated
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
  @Transactional
  public User create(@Valid UserCommand userCommand) {
    String username = userCommand.username();
    String password = userCommand.password();
    String email = userCommand.email();

    BinaryContent profile = userCommand.profile().stream()
        .map(dto -> binaryContentRepository.save(new BinaryContent(
            dto.fileName(),
            dto.contentType(),
            dto.bytes(),
            dto.bytes().length
        )))
        .findFirst()
        .orElse(null);

    User user = new User(username, email, password, profile);
    UserStatus userStatus = new UserStatus();
    userStatus.setLastActiveAt(Instant.now());
    user.attachStatus(userStatus);

    return userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserFindResponse findById(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> new NoSuchElementException("findById : 유저를 찾을 수 없습니다. [" + userId + "]"));

    return UserFindResponse.builder()
        .id(user.getId())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .username(user.getUsername())
        .email(user.getEmail())
        .profileId(user.getProfile().getId())
        .online(userStatusRepository.findByUserId(user.getId())
            .orElseThrow(() -> new NoSuchElementException(
                "findById : UserStatus를 찾을 수 없습니다. [" + user.getId() + "]"))
            .isOnline())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserFindResponse> findAll() {
    List<UserFindResponse> userFindResponses = new ArrayList<>();
    for (User user : userRepository.findAll()) {
      UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
          .orElseThrow(() -> new NoSuchElementException(
              "findAll : UserStatus를 찾을 수 없습니다. [" + user.getId() + "]"));
      userFindResponses.add(UserFindResponse.builder()
          .id(user.getId())
          .createdAt(user.getCreatedAt())
          .updatedAt(user.getUpdatedAt())
          .username(user.getUsername())
          .email(user.getEmail())
          .profileId(user.getProfile().getId())
          .online(userStatus.isOnline())
          .build());
    }
    return userFindResponses;
  }

  @Override
  @Transactional
  public User update(UUID userId, @Valid UserCommand userCommand) {
    String newUserName = userCommand.username();
    String newPassword = userCommand.password();
    String newEmail = userCommand.email();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("update : 유저를 찾을 수 없습니다. [" + userId + "]"));

    BinaryContent newProfile = userCommand.profile().stream()
        .map(dto -> binaryContentRepository.save(new BinaryContent(
            dto.fileName(),
            dto.contentType(),
            dto.bytes(),
            dto.bytes().length
        )))
        .findFirst()
        .orElse(null);

    if (newProfile != null) {
      binaryContentRepository.deleteById(user.getProfile().getId());
    }

    user.update(newUserName, newEmail, newPassword, newProfile);

    return userRepository.save(user);
  }

  @Override
  @Transactional
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("delete : 유저를 찾을 수 없습니다. [" + userId + "]"));

    if (user.getProfile() != null) {
      binaryContentRepository.deleteById(user.getProfile().getId());
    }

    userStatusRepository.findByUserId(user.getId())
        .ifPresent(userStatus -> userStatusRepository.deleteById(userStatus.getId()));

    userRepository.deleteById(user.getId());
  }

  private void validateExist(String username, String email) {
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException(
          "validateUnique : 이미 존재하는 username 입니다. [" + username + "]");
    }
    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("validateUnique : 이미 존재하는 email 입니다. [" + email + "]");
    }
  }
}
