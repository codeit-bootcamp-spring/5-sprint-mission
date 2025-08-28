// package com.sprint.mission.discodeit.service.user;
//
// import static com.sprint.mission.discodeit.support.StringUtil.nullOrStrip;
// import static com.sprint.mission.discodeit.support.StringUtil.nullOrStripAndLowerCase;
//
// import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
// import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
// import com.sprint.mission.discodeit.dto.user.UserDto;
// import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
// import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
// import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
// import com.sprint.mission.discodeit.entity.BinaryContent;
// import com.sprint.mission.discodeit.entity.User;
// import com.sprint.mission.discodeit.entity.UserStatus;
// import com.sprint.mission.discodeit.exception.DuplicateResourceException;
// import com.sprint.mission.discodeit.repository.BinaryContentRepository;
// import com.sprint.mission.discodeit.repository.UserRepository;
// import com.sprint.mission.discodeit.repository.UserStatusRepository;
// import com.sprint.mission.discodeit.support.FileNames;
// import java.io.IOException;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;
// import java.util.UUID;
// import java.util.stream.Collectors;
// import lombok.RequiredArgsConstructor;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.multipart.MultipartFile;
//
// @Service
// @RequiredArgsConstructor
// @Transactional(readOnly = true)
// public class UserService {
//
//   private final UserRepository userRepository;
//   private final UserStatusRepository userStatusRepository;
//   private final BinaryContentRepository binaryContentRepository;
//
//   private final PasswordEncoder passwordEncoder;
//
//   public UserDto toResponse(User user) {
//     UserStatusType userStatusType = userStatusRepository.findByUserId(user.getId())
//       .map(UserStatus::getType)
//       .orElse(UserStatusType.OFFLINE);
//     BinaryContent bc = binaryContentRepository.find(user.getProfile()).orElse(null);
//     BinaryContentDto binaryContentDto = bc != null ? BinaryContentDto.from(bc) : null;
//     return UserDto.from(user, userStatusType, binaryContentDto);
//   }
//
//   public List<UserDto> findByUsername(String username) {
//     String u = nullOrStripAndLowerCase(username);
//     return userRepository.findByUsername(u)
//       .map(this::toResponse)
//       .map(List::of)
//       .orElse(List.of());
//   }
//
//   public List<UserDto> findByEmail(String email) {
//     String e = nullOrStripAndLowerCase(email);
//     return userRepository.findByEmail(e)
//       .map(this::toResponse)
//       .map(List::of)
//       .orElse(List.of());
//   }
//
//   public List<UserDto> findAll() {
//     List<User> users = userRepository.findAll();
//
//     if (users.isEmpty()) {
//       return List.of();
//     }
//
//     Set<UUID> userIds = users.stream()
//       .map(User::getId)
//       .collect(Collectors.toSet());
//
//     Map<UUID, UserStatusType> userStatusTypeMap =
//       userStatusRepository.findAllTypesByUserIds(userIds);
//
//     return users.stream()
//       .map(u -> {
//         UserStatusType userStatusType = userStatusTypeMap.getOrDefault(u.getId(),
//           UserStatusType.OFFLINE);
//         BinaryContent bc = binaryContentRepository.find(u.getProfile()).orElse(null);
//         BinaryContentDto binaryContentDto = bc != null ? BinaryContentDto.from(bc) : null;
//         return UserDto.from(u, userStatusType, binaryContentDto);
//       })
//       .toList();
//   }
//
//   @Transactional
//   public UserDto create(UserCreateRequest req, MultipartFile profile)
//     throws IOException {
//     String username = nullOrStripAndLowerCase(req.username());
//     if (userRepository.existsByUsername(username)) {
//       throw new DuplicateResourceException(
//         "User with username %s already exists".formatted(username));
//     }
//
//     String email = nullOrStripAndLowerCase(req.email());
//     if (userRepository.existsByEmail(email)) {
//       throw new DuplicateResourceException(
//         "User with email %s already exists".formatted(email));
//     }
//
//     UUID profileId;
//     if (profile != null && !profile.isEmpty()) {
//       String ct = FileNames.normalizeContentType(profile.getContentType());
//       String original = profile.getOriginalFileName();
//       String fileName = FileNames.buildStoredName(original, ct);
//       profileId = binaryContentRepository.save(
//         new BinaryContent(fileName, ct, profile.getBytes())
//       ).getId();
//     } else {
//       profileId = null;
//     }
//
//     String password = passwordEncoder.encode(req.password());
//     User user = new User(
//       email,
//       username,
//       password,
//       profileId
//     );
//
//     User saved = userRepository.save(user);
//     UserStatus us = userStatusRepository.save(new UserStatus(saved.getId()).login());
//     BinaryContent bc = binaryContentRepository.find(saved.getProfile()).orElse(null);
//     BinaryContentDto binaryContentDto = bc != null ? BinaryContentDto.from(bc) : null;
//     return UserDto.from(saved, us.getType(), binaryContentDto);
//   }
//
//   public UserDto find(UUID userId) {
//     return toResponse(userRepository.getOrThrow(userId));
//   }
//
//   @Transactional
//   public void delete(UUID userId) {
//     User user = userRepository.getOrThrow(userId);
//
//     guildRepository.deleteAllByOwnerId(user.getId());
//
//     friendRequestRepository.deleteAllByUserId(user.getId());
//
//     if (user.getProfile() != null) {
//       binaryContentRepository.delete(user.getProfile());
//     }
//
//     userStatusRepository.deleteByUserId(user.getId());
//
//     userRepository.delete(user.getId());
//   }
//
//   @Transactional
//   public UserDto update(UUID userId, UserUpdateRequest req, MultipartFile profile)
//     throws IOException {
//     User u = userRepository.getOrThrow(userId);
//
//     String oldUsername = nullOrStripAndLowerCase(u.getUsername());
//     String newUsername = req != null ? nullOrStripAndLowerCase(req.newUsername()) : null;
//     String username = newUsername != null && !newUsername.equals(oldUsername) ? newUsername : null;
//     if (username != null && userRepository.existsByUsername(username)) {
//       throw new DuplicateResourceException(
//         "User with username %s already exists".formatted(username));
//     }
//
//     String oldEmail = nullOrStripAndLowerCase(u.getEmail());
//     String newEmail = req != null ? nullOrStripAndLowerCase(req.newEmail()) : null;
//     String email = newEmail != null && !newEmail.equals(oldEmail) ? newEmail : null;
//     if (email != null && userRepository.existsByEmail(email)) {
//       throw new DuplicateResourceException("User with email %s already exists".formatted(email));
//     }
//
//     String oldPassword = u.getPassword();
//     String newPassword = req != null ? nullOrStrip(req.newPassword()) : null;
//     String password =
//       newPassword != null && !passwordEncoder.matches(newPassword, oldPassword)
//         ? passwordEncoder.encode(newPassword)
//         : null;
//
//     UUID profileId;
//     if (profile != null && !profile.isEmpty()) {
//       String ct = FileNames.normalizeContentType(profile.getContentType());
//       String original = profile.getOriginalFileName();
//       String fileName = FileNames.buildStoredName(original, ct);
//       profileId = binaryContentRepository.save(
//         new BinaryContent(fileName, ct, profile.getBytes())
//       ).getId();
//     } else {
//       profileId = null;
//     }
//
//     boolean noOp = username == null
//       && email == null
//       && password == null
//       && profileId == null;
//     if (noOp) {
//       return toResponse(u);
//     }
//
//     if (profileId != null && u.getProfile() != null) {
//       binaryContentRepository.delete(u.getProfile());
//     }
//
//     return toResponse(
//       userRepository.save(
//         u.update(username, email, password, profileId)
//       )
//     );
//   }
//
//   @Transactional
//   public UserStatusDto updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest req) {
//     userRepository.getOrThrow(userId);
//
//     UserStatus us = userStatusRepository.findByUserId(userId)
//       .orElseGet(() -> userStatusRepository.save(new UserStatus(userId)));
//
//     if (req.newUserStatusType() != null) {
//       us.setType(req.newUserStatusType());
//     }
//
//     if (req.newLastActiveAt() != null) {
//       us.setLastActiveAt(req.newLastActiveAt());
//     }
//
//     return UserStatusDto.from(userStatusRepository.save(us));
//   }
//
//   @Transactional
//   public void heartbeat(UUID userId) {
//     userRepository.getOrThrow(userId);
//
//     UserStatus us = userStatusRepository.findByUserId(userId)
//       .orElseGet(() -> userStatusRepository.save(new UserStatus(userId)));
//
//     userStatusRepository.save(us.heartbeat());
//   }
// }
