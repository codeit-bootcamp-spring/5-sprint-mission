package com.sprint.mission.discodeit.service.user;

import static com.sprint.mission.discodeit.support.StringUtil.nullOrStripAndLowerCase;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.enums.UserStatusType;
import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.dto.response.user.UserSaveResponse;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.NotFoundException;
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
    UserStatus userStatus = userStatusRepository.getOrThrowByUserId(user.getId());
    return UserResponse.from(user, userStatus.getType());
  }

  public List<UserResponse> findAll() {
    List<User> users = userRepository.findAll();
    if (users.isEmpty()) {
      return List.of();
    }
    Set<UUID> ids = users.stream().map(User::getId).collect(Collectors.toSet());

    Map<UUID, UserStatusType> statusMap = userStatusRepository.findAllByUserIds(ids).stream()
        .collect(Collectors.toMap(
            UserStatus::getUserId,
            UserStatus::getType
        ));

    return users.stream()
        .map(u -> UserResponse.from(u, statusMap.get(u.getId())))
        .toList();
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

  @Transactional
  public UserSaveResponse create(UserCreateRequest req, MultipartFile profile)
      throws IOException {
    String email = nullOrStripAndLowerCase(req.email());
    if (userRepository.existsByEmail(email)) {
      throw new DuplicateResourceException("User with email %s already exists".formatted(email));
    }
    String username = nullOrStripAndLowerCase(req.username());
    if (userRepository.existsByUsername(username)) {
      throw new DuplicateResourceException(
          "User with username %s already exists".formatted(username));
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
    return userRepository.findById(userId)
        .map(this::toResponse)
        .orElseThrow(() -> new NotFoundException("User with id %s not found".formatted(userId)));
  }

  @Transactional
  public void deleteAccount(UUID userId) {
    User user = userRepository.getOrThrow(userId);

    guildRepository.findGuildsOwnedByUser(user.getId())
        .forEach(g -> guildRepository.softDeleteById(g.getId()));

    friendRequestRepository.softDeleteAllByUserId(user.getId());

    if (user.getProfileId() != null) {
      binaryContentRepository.softDeleteById(user.getProfileId());
    }

    userStatusRepository.softDeleteByUserId(user.getId());

    userRepository.softDeleteById(user.getId());
  }

  // @Transactional
  // public UserSaveResponse update(UUID userId, UserUpdateRequest req, MultipartFile profile)
  //     throws IOException {
  //   User u = userRepository.getOrThrow(userId);
  //   String email;
  //   if (req.newEmail() != null && !req.newEmail().equals(u.getEmail())) {
  //     email = null;
  //   } else {
  //     email = nullOrStripAndLowerCase(req.newEmail());
  //   }
  //   String email = nullOrStripAndLowerCase(req.newEmail());
  //   if (userRepository.existsByEmail(email)) {
  //     throw new DuplicateResourceException("User with email %s already exists".formatted(email));
  //   }
  //   String username = nullOrStripAndLowerCase(req.username());
  //   if (userRepository.existsByUsername(username)) {
  //     throw new DuplicateResourceException(
  //         "User with username %s already exists".formatted(username));
  //   }
  //
  //   UUID profileId = null;
  //
  //   if (profile != null && !profile.isEmpty()) {
  //     String ct = FileNames.normalizeContentType(profile.getContentType());
  //     String original = profile.getOriginalFilename();
  //     String fileName = FileNames.buildStoredName(original, ct);
  //
  //     try {
  //       profileId = binaryContentService.create(
  //           new BinaryContentCreateRequest(fileName, ct, profile.getBytes())).id();
  //     } catch (IOException e) {
  //       throw new IOException("Failed to read profile image", e);
  //     }
  //   }
  //
  //   User saved = userRepository.save(user);
  //
  //   UserStatus userStatus = new UserStatus(saved.getId());
  //   userStatusRepository.save(userStatus);
  //
  //   return UserSaveResponse.from(saved);
  // }
  //
  // @Transactional
  // public void updateEmail(UUID userId, UserUpdateEmailRequest req) {
  //   String email = nullOrStripAndLowerCase(req.email());
  //   User user = userRepository.getOrThrow(userId);
  //
  //   if (user.getEmail().equals(email)) {
  //     throw new IllegalArgumentException("기존과 동일한 이메일입니다");
  //   }
  //   if (userRepository.existsByEmail(email)) {
  //     throw new DuplicateResourceException("User with email %s already exists");
  //   }
  //   try {
  //     update(userId, u -> u.changeEmail(email));
  //   } catch (DataIntegrityViolationException e) {
  //     throw new DuplicateResourceException("중복된 이메일이 존재합니다.");
  //   }
  // }
  //
  // @Transactional
  // public void updateUsername(UUID userId, UserUpdateUsernameRequest req) {
  //   String username = nullOrStripAndLowerCase(req.username());
  //   User user = userRepository.getOrThrow(userId);
  //
  //   if (user.getUsername().equals(username)) {
  //     throw new IllegalArgumentException("기존과 동일한 사용자명입니다.");
  //   }
  //
  //   if (userRepository.existsByUsername(username)) {
  //     throw new DuplicateResourceException("중복된 사용자명이 존재합니다.");
  //   }
  //
  //   try {
  //     update(userId, u -> u.changeUsername(username));
  //   } catch (DataIntegrityViolationException e) {
  //     throw new DuplicateResourceException("중복된 사용자명이 존재합니다.");
  //   }
  // }
  //
  // @Transactional
  // public void updatePassword(UUID userId, UserUpdatePasswordRequest req) {
  //   String password = req.password().strip();
  //   User user = userRepository.getOrThrow(userId);
  //   if (passwordEncoder.matches(password, user.getPassword())) {
  //     throw new DuplicateResourceException("동일한 비밀번호입니다.");
  //   }
  //   update(userId, u -> u.changePassword(passwordEncoder.encode(password)));
  // }
}