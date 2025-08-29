package com.sprint.mission.discodeit.service;

import static com.sprint.mission.discodeit.support.Utils.toBinaryContentFromMultipartFile;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelParticipantRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final BinaryContentRepository binaryContentRepository;
    private final ChannelParticipantRepository channelParticipantRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> findAll() {
        return userRepository.findAllToDto(Instant.now().minus(Duration.ofMinutes(5)));
    }

    @Transactional
    public UserDto create(UserCreateRequest req, MultipartFile profile) {
        String username = req.username().strip().toLowerCase(Locale.ROOT);
        String email = req.email().strip().toLowerCase(Locale.ROOT);
        String password = passwordEncoder.encode(req.password());
        BinaryContent savedProfile = (profile != null && !profile.isEmpty())
            ? binaryContentRepository.save(toBinaryContentFromMultipartFile(profile))
            : null;

        User savedUser = userRepository.save(new User(
            username,
            email,
            password,
            savedProfile
        ));

        UserStatus savedUserStatus = userStatusRepository.save(new UserStatus(savedUser));

        return UserDto.from(savedUser, savedUserStatus);
    }

    @Transactional
    public void delete(UUID userId) {
        User user = userRepository.getOrThrowForUpdate(userId);

        UUID profileId;
        if (user.getProfile() != null) {
            profileId = user.getProfile().getId();
            user.setProfile(null);
        } else {
            profileId = null;
        }

        messageRepository.nullifyAllAuthorByUserId(userId);

        readStatusRepository.deleteAllByUserId(userId);

        channelParticipantRepository.deleteAllByUserId(userId);

        userStatusRepository.deleteAllByUserId(userId);

        if (profileId != null) {
            binaryContentRepository.deleteIfExists(profileId);
        }

        userRepository.delete(user);
    }

    @Transactional
    public UserDto update(UUID userId, UserUpdateRequest req, MultipartFile profile) {
        User u = userRepository.getOrThrowForUpdate(userId);

        String newUsername = (req.newUsername() != null && !req.newUsername().isBlank())
            ? req.newUsername().strip().toLowerCase(Locale.ROOT)
            : null;
        if (newUsername != null && !newUsername.equals(u.getUsername())) {
            u.setUsername(newUsername);
        }

        String newEmail = (req.newEmail() != null && !req.newEmail().isBlank())
            ? req.newEmail().strip().toLowerCase(Locale.ROOT)
            : null;
        if (newEmail != null && !newEmail.equals(u.getEmail())) {
            u.setEmail(newEmail);
        }

        String newPassword = req.newPassword();
        if (newPassword != null
            && !newPassword.isBlank()
            && !passwordEncoder.matches(newPassword, u.getPassword())
        ) {
            u.setPassword(passwordEncoder.encode(newPassword));
        }

        BinaryContent newProfile = (profile != null && !profile.isEmpty())
            ? binaryContentRepository.save(toBinaryContentFromMultipartFile(profile))
            : null;

        if (newProfile != null) {
            UUID oldProfileId = (u.getProfile() != null) ? u.getProfile().getId() : null;
            u.setProfile(newProfile);
            if (oldProfileId != null) {
                binaryContentRepository.deleteIfExists(oldProfileId);
            }
        }

        return UserDto.from(u, userStatusRepository.getOrThrowByUserId(u.getId()));
    }

    @Transactional
    public UserStatusDto updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest req) {
        UserStatus us = userStatusRepository.getOrThrowByUserId(userId);

        if (req.newLastActiveAt() != null) {
            us.setLastActiveAt(req.newLastActiveAt());
        }

        return UserStatusDto.from(us);
    }
}
