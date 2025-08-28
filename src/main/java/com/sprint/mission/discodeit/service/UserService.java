package com.sprint.mission.discodeit.service;

import static com.sprint.mission.discodeit.support.Utils.toBinaryContentFromMultipartFile;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
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
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> findAll() {
        return userRepository.findAllDto(Instant.now().minus(Duration.ofMinutes(5)));
    }

    @Transactional
    public UserDto create(UserCreateRequest req, MultipartFile profile) {
        String username = req.username().strip().toLowerCase(Locale.ROOT);
        String email = req.email().strip().toLowerCase(Locale.ROOT);
        String password = passwordEncoder.encode(req.password());
        BinaryContent savedProfile;
        if (profile != null && !profile.isEmpty()) {
            savedProfile = binaryContentRepository.save(
                toBinaryContentFromMultipartFile(profile)
            );
        } else {
            savedProfile = null;
        }

        User savedUser = userRepository.save(new User(
            username,
            email,
            password,
            savedProfile
        ));

        UserStatus savedUserStatus = userStatusRepository.save(new UserStatus(savedUser));

        return UserDto.from(savedUser, savedUserStatus);
    }
//
//    @Transactional
//    public void delete(UUID userId) {
//        User user = userRepository.getOrThrow(userId);
//
//        guildRepository.deleteAllByOwnerId(user.getId());
//
//        friendRequestRepository.deleteAllByUserId(user.getId());
//
//        if (user.getProfile() != null) {
//            binaryContentRepository.delete(user.getProfile());
//        }
//
//        userStatusRepository.deleteByUserId(user.getId());
//
//        userRepository.delete(user.getId());
//    }
//
//    @Transactional
//    public UserDto update(UUID userId, UserUpdateRequest req, MultipartFile profile)
//        throws IOException {
//        User u = userRepository.getOrThrow(userId);
//
//        String oldUsername = nullOrStripAndLowerCase(u.getUsername());
//        String newUsername = req != null ? nullOrStripAndLowerCase(req.newUsername()) : null;
//        String username =
//            newUsername != null && !newUsername.equals(oldUsername) ? newUsername : null;
//        if (username != null && userRepository.existsByUsername(username)) {
//            throw new DuplicateResourceException(
//                "User with username %s already exists".formatted(username));
//        }
//
//        String oldEmail = nullOrStripAndLowerCase(u.getEmail());
//        String newEmail = req != null ? nullOrStripAndLowerCase(req.newEmail()) : null;
//        String email = newEmail != null && !newEmail.equals(oldEmail) ? newEmail : null;
//        if (email != null && userRepository.existsByEmail(email)) {
//            throw new DuplicateResourceException(
//                "User with email %s already exists".formatted(email));
//        }
//
//        String oldPassword = u.getPassword();
//        String newPassword = req != null ? nullOrStrip(req.newPassword()) : null;
//        String password =
//            newPassword != null && !passwordEncoder.matches(newPassword, oldPassword)
//                ? passwordEncoder.encode(newPassword)
//                : null;
//
//        UUID profileId;
//        if (profile != null && !profile.isEmpty()) {
//            String ct = FileNames.normalizeContentType(profile.getContentType());
//            String original = profile.getOriginalFileName();
//            String fileName = FileNames.buildStoredName(original, ct);
//            profileId = binaryContentRepository.save(
//                new BinaryContent(fileName, ct, profile.getBytes())
//            ).getId();
//        } else {
//            profileId = null;
//        }
//
//        boolean noOp = username == null
//            && email == null
//            && password == null
//            && profileId == null;
//        if (noOp) {
//            return toResponse(u);
//        }
//
//        if (profileId != null && u.getProfile() != null) {
//            binaryContentRepository.delete(u.getProfile());
//        }
//
//        return toResponse(
//            userRepository.save(
//                u.update(username, email, password, profileId)
//            )
//        );
//    }
//
//    @Transactional
//    public UserStatusDto updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest req) {
//        userRepository.getOrThrow(userId);
//
//        UserStatus us = userStatusRepository.findByUserId(userId)
//            .orElseGet(() -> userStatusRepository.save(new UserStatus(userId)));
//
//        if (req.newUserStatusType() != null) {
//            us.setType(req.newUserStatusType());
//        }
//
//        if (req.newLastActiveAt() != null) {
//            us.setLastActiveAt(req.newLastActiveAt());
//        }
//
//        return UserStatusDto.from(userStatusRepository.save(us));
//    }
//
//    @Transactional
//    public void heartbeat(UUID userId) {
//        userRepository.getOrThrow(userId);
//
//        UserStatus us = userStatusRepository.findByUserId(userId)
//            .orElseGet(() -> userStatusRepository.save(new UserStatus(userId)));
//
//        userStatusRepository.save(us.heartbeat());
//    }
}
