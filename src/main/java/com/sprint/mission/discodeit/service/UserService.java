package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserProfileUploadException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Service
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
        String username = request.username().strip().toLowerCase(Locale.ROOT);
        String email = request.email().strip().toLowerCase(Locale.ROOT);

        log.debug("사용자 생성 요청: username={}, email={}", username, email);

        String password = passwordEncoder.encode(request.password());

        BinaryContent savedProfile = null;
        if (profile != null && !profile.isEmpty()) {
            savedProfile = saveProfileImage(profile);
        }

        User savedUser;
        try {
            savedUser = userRepository.save(
                new User(
                    username,
                    email,
                    password,
                    savedProfile
                )
            );
            userRepository.flush();
        } catch (DataIntegrityViolationException e) {
            handleDuplicateUserConstraint(e, username, email);
            throw e;
        }

        log.info("사용자 생성 완료: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
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
        log.debug("사용자 수정 요청: userId={}", userId);

        User user = getUserOrThrow(userId);

        BinaryContent newProfile = null;
        if (profile != null && !profile.isEmpty()) {
            newProfile = saveProfileImage(profile);
        }

        String newUsername = null;
        if (hasText(request.newUsername())) {
            newUsername = request.newUsername().strip().toLowerCase(Locale.ROOT);
        }

        String newEmail = null;
        if (request.newEmail() != null && !request.newEmail().isBlank()) {
            newEmail = request.newEmail().strip().toLowerCase(Locale.ROOT);
        }

        try {
            updateUser(user, request, newProfile, newUsername, newEmail);
            userRepository.flush();
        } catch (DataIntegrityViolationException e) {
            handleDuplicateUserConstraint(e, newUsername, newEmail);
            throw e;
        }

        log.info("사용자 수정 완료: userId={}", userId);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserStatusDto updateUserStatusByUserId(
        UUID userId,
        UserStatusUpdateRequest request
    ) {
        User user = getUserOrThrow(userId);
        UserStatus userStatus = user.getUserStatus();

        if (request.newLastActiveAt() != null) {
            userStatus.update(request.newLastActiveAt());
        }

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    public void delete(UUID userId) {
        log.debug("사용자 삭제 요청: userId={}", userId);

        User user = getUserOrThrow(userId);

        messageRepository.nullifyAuthorByUser(user);
        readStatusRepository.deleteAllByUser(user);

        userRepository.delete(user);

        log.info("사용자 삭제 완료: userId={}", userId);
    }

    private BinaryContent saveProfileImage(MultipartFile profile) {
        log.debug("프로필 이미지 업로드 시도: filename={}, size={}",
            profile.getOriginalFilename(), profile.getSize());

        BinaryContent savedProfile = binaryContentRepository.save(
            new BinaryContent(
                profile.getOriginalFilename(),
                profile.getSize(),
                profile.getContentType()
            )
        );

        try {
            binaryContentStorage.put(savedProfile.getId(), profile.getBytes());
        } catch (IOException e) {
            throw new UserProfileUploadException(e);
        }

        log.info("프로필 이미지 저장 완료: binaryContentId={}, size={}",
            savedProfile.getId(), savedProfile.getSize());

        return savedProfile;
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    private void updateUser(
        User user,
        UserUpdateRequest request,
        BinaryContent newProfile,
        String newUsername,
        String newEmail
    ) {
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

    private void handleDuplicateUserConstraint(
        DataIntegrityViolationException e,
        String username,
        String email
    ) {
        String message = e.getMessage();
        if (message == null) {
            throw e;
        }

        String lowerMessage = message.toLowerCase(Locale.ROOT);

        if (lowerMessage.contains("users_email_key")) {
            log.warn("중복된 이메일: email={}", email);
            throw new DuplicateEmailException(email);
        }

        if (lowerMessage.contains("users_username_key")) {
            log.warn("중복된 사용자명: username={}", username);
            throw new DuplicateUsernameException(username);
        }

        if (lowerMessage.contains("email")) {
            log.warn("중복된 이메일: email={}", email);
            throw new DuplicateEmailException(email);
        }

        if (lowerMessage.contains("username")) {
            log.warn("중복된 사용자명: username={}", username);
            throw new DuplicateUsernameException(username);
        }

        throw e;
    }
}
