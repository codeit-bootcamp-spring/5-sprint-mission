package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    private final BinaryContentStorage binaryContentStorage;
    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;
    private final UserStatusMapper userStatusMapper;

    @Transactional
    public UserDto create(
        UserCreateRequest request,
        MultipartFile profile
    ) {
        log.info("Creating user. Email: {}, Username: {}", request.email(), request.username());

        String username = request.username().strip().toLowerCase(Locale.ROOT);
        String email = request.email().strip().toLowerCase(Locale.ROOT);
        String password = passwordEncoder.encode(request.password());

        BinaryContent savedProfile = null;
        if (profile != null && !profile.isEmpty()) {
            savedProfile = saveProfileImage(profile);
        }

        User savedUser = userRepository.save(
            new User(username, email, password, savedProfile)
        );

        log.info("User created successfully. UserId: {}", savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    public List<UserDto> findAll() {
        log.debug("Finding all users");

        Instant onlineSince = Instant.now().minus(Duration.ofMinutes(5));

        return userRepository.findAllGraph()
            .stream()
            .map(u -> userMapper.toDto(u, onlineSince))
            .toList();
    }

    @Transactional
    public UserDto update(
        UUID userId,
        UserUpdateRequest request,
        MultipartFile profile
    ) {
        log.info("Updating user. UserId: {}", userId);

        User user = userRepository.getOrThrow(userId);

        BinaryContent newProfile = null;
        if (profile != null && !profile.isEmpty()) {
            newProfile = saveProfileImage(profile);
        }

        updateUser(user, request, newProfile);

        return userMapper.toDto(user);
    }

    @Transactional
    public UserStatusDto updateUserStatusByUserId(
        UUID userId,
        UserStatusUpdateRequest request
    ) {
        log.debug("Updating user status. UserId: {}", userId);

        User user = userRepository.getOrThrow(userId);
        UserStatus userStatus = user.getUserStatus();

        if (request.newLastActiveAt() != null) {
            userStatus.setLastActiveAt(request.newLastActiveAt());
        }

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    public void delete(UUID userId) {
        log.info("Deleting user. UserId: {}", userId);

        User user = userRepository.getOrThrow(userId);

        messageRepository.nullifyAuthorByUser(user);
        readStatusRepository.deleteAllByUser(user);

        userRepository.delete(user);
        log.info("User deleted successfully. UserId: {}", userId);
    }

    private BinaryContent saveProfileImage(MultipartFile profile) {
        log.info("Uploading profile image. FileName: {}", profile.getOriginalFilename());

        BinaryContent savedContent = binaryContentRepository.save(
            new BinaryContent(
                profile.getOriginalFilename(),
                profile.getSize(),
                profile.getContentType()
            )
        );

        try {
            binaryContentStorage.put(savedContent.getId(), profile.getBytes());
        } catch (IOException e) {
            log.error("Failed to save profile image file. ContentId: {}", savedContent.getId(), e);
            throw new UncheckedIOException("프로필 파일 저장 실패: " + savedContent.getId(), e);
        }

        return savedContent;
    }

    private void updateUser(
        User user,
        UserUpdateRequest request,
        BinaryContent newProfile
    ) {
        String newUsername = null;
        if (hasText(request.newUsername())) {
            newUsername = request.newUsername().strip().toLowerCase(Locale.ROOT);
        }

        String newEmail = null;
        if (request.newEmail() != null && !request.newEmail().isBlank()) {
            newEmail = request.newEmail().strip().toLowerCase(Locale.ROOT);
        }

        String newEncodedPassword = null;
        if (request.newPassword() != null
            && !request.newPassword().isBlank()
            && !passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            newEncodedPassword = passwordEncoder.encode(request.newPassword());
        }

        user.update(
            newUsername,
            newEmail,
            newEncodedPassword,
            newProfile
        );
    }
}
