package com.sprint.mission.discodeit.service.user;

import static com.sprint.mission.discodeit.support.StringUtil.nullOrStripAndLowerCase;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.enums.UserStatusType;
import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.dto.response.user.UserSaveResponse;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.FriendRequestRepository;
import com.sprint.mission.discodeit.repository.GuildRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.binarycontent.BinaryContentService;
import com.sprint.mission.discodeit.support.FileNames;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final BinaryContentService binaryContentService;
  private final UserStatusRepository userStatusRepository;
  private final FriendRequestRepository friendRequestRepository;
  private final GuildRepository guildRepository;
  private final BinaryContentRepository binaryContentRepository;

  private final PasswordEncoder passwordEncoder;

  private UserResponse toResponse(User user) {
    UserStatusType userStatusType = userStatusRepository.findByUserId(user.getId())
        .map(UserStatus::getType)
        .orElse(UserStatusType.OFFLINE);
    return UserResponse.from(user, userStatusType);
  }

  public List<UserResponse> findByUsername(String username) {
    return userRepository.findByUsername(username)
        .map(this::toResponse)
        .map(List::of)
        .orElse(List.of());
  }

  public List<UserResponse> findByEmail(String email) {
    return userRepository.findByEmail(email)
        .map(this::toResponse)
        .map(List::of)
        .orElse(List.of());
  }

  public List<UserResponse> findAll() {
    List<User> users = userRepository.findAll();
    if (users.isEmpty()) {
      return List.of();
    }
    Set<UUID> ids = users.stream()
        .map(User::getId)
        .collect(Collectors.toSet()
        );

    Map<UUID, UserStatusType> statusMap = userStatusRepository.findAllByUserId(ids).stream()
        .collect(Collectors.toMap(
            UserStatus::getUserId,
            UserStatus::getType
        ));

    return users.stream()
        .map(u -> {
          UserStatusType type = statusMap.getOrDefault(u.getId(), UserStatusType.OFFLINE);
          return UserResponse.from(u, type);
        })
        .toList();
  }

  @Transactional
  public UserSaveResponse create(UserCreateRequest req, MultipartFile profile)
      throws IOException {
    String username = nullOrStripAndLowerCase(req.username());
    if (userRepository.existsByUsername(username)) {
      throw new DuplicateResourceException(
          "User with username %s already exists".formatted(username));
    }

    String email = nullOrStripAndLowerCase(req.email());
    if (userRepository.existsByEmail(email)) {
      throw new DuplicateResourceException("User with email %s already exists".formatted(email));
    }
    UUID profileId = null;

    if (profile != null && !profile.isEmpty()) {
      String ct = FileNames.normalizeContentType(profile.getContentType());
      String original = profile.getOriginalFilename();
      String fileName = FileNames.buildStoredName(original, ct);

      try {
        profileId = binaryContentService.create(
            new BinaryContentCreateRequest(fileName, ct, profile.getBytes())).id();
      } catch (IOException e) {
        throw new IOException("Failed to read profile image", e);
      }
    }

    User user = new User(
        email,
        username,
        passwordEncoder.encode(req.password().strip()),
        profileId
    );

    User saved = userRepository.save(user);

    UserStatus userStatus = new UserStatus(saved.getId());
    userStatusRepository.save(userStatus);

    return UserSaveResponse.from(saved);
  }

  public UserResponse find(UUID userId) {
    return toResponse(userRepository.getOrThrow(userId));
  }

  @Transactional
  public void deleteAccount(UUID userId) {
    User user = userRepository.getOrThrow(userId);

    guildRepository.softDeleteAllById(user.getGuildIds());

    friendRequestRepository.softDeleteAllByUserId(user.getId());

    if (user.getProfileId() != null) {
      binaryContentRepository.softDeleteById(user.getProfileId());
    }

    userStatusRepository.softDeleteByUserId(user.getId());

    userRepository.softDeleteById(user.getId());
  }

  @Transactional
  public UserSaveResponse update(UUID userId, UserUpdateRequest req, MultipartFile profile)
      throws IOException {
    User u = userRepository.getOrThrow(userId);

    String username;
    if (req != null
        && req.newUsername() != null
        && !req.newUsername().equals(u.getUsername())
    ) {
      username = nullOrStripAndLowerCase(req.newUsername());
    } else {
      username = null;
    }

    if (username != null && userRepository.existsByUsername(username)) {
      throw new DuplicateResourceException(
          "User with username %s already exists".formatted(username));
    }

    String email;
    if (req != null
        && req.newEmail() != null
        && !req.newEmail().equals(u.getEmail())
    ) {
      email = nullOrStripAndLowerCase(req.newEmail());
    } else {
      email = null;
    }
    if (req != null
        && email != null
        && userRepository.existsByEmail(email)
    ) {
      throw new DuplicateResourceException("User with email %s already exists".formatted(email));
    }

    String password;
    if (req != null
        && req.newPassword() != null
        && !passwordEncoder.matches(req.newPassword().strip(), u.getPassword())
    ) {
      password = passwordEncoder.encode(req.newPassword().strip());
    } else {
      password = null;
    }

    UUID profileId;
    if (profile != null && !profile.isEmpty()) {
      String ct = FileNames.normalizeContentType(profile.getContentType());
      String original = profile.getOriginalFilename();
      String fileName = FileNames.buildStoredName(original, ct);
      profileId = binaryContentService.create(
          new BinaryContentCreateRequest(fileName, ct, profile.getBytes())).id();
    } else {
      profileId = null;
    }

    u.update(username, email, password, profileId);

    return UserSaveResponse.from(userRepository.save(u));
  }
}
