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
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.io.UncheckedIOException;
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
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    private final BinaryContentStorage binaryContentStorage;

    private final UserMapper userMapper;
    private final UserStatusMapper userStatusMapper;

    private final PasswordEncoder passwordEncoder;

    public List<UserDto> findAll() {
        Instant onlineSince = Instant.now().minus(Duration.ofMinutes(5));

        return userRepository.findAllGraph()
            .stream()
            .map(u -> userMapper.toDto(u, onlineSince))
            .toList();
    }

    @Transactional
    public UserDto create(UserCreateRequest req, MultipartFile profile) {
        String username = req.username().strip().toLowerCase(Locale.ROOT);
        String email = req.email().strip().toLowerCase(Locale.ROOT);
        String password = passwordEncoder.encode(req.password());

        BinaryContent savedProfile = null;
        if (profile != null && !profile.isEmpty()) {
            savedProfile = binaryContentRepository.save(
                new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getSize(),
                    profile.getContentType()
                )
            );

            try {
                binaryContentStorage.put(savedProfile.getId(), profile.getBytes());
            } catch (IOException e) {
                throw new UncheckedIOException("프로필 파일 저장 실패: " + savedProfile.getId(), e);
            }
        }

        return userMapper.toDto(
            userRepository.save(
                new User(username, email, password, savedProfile)
            )
        );
    }

    @Transactional
    public void delete(UUID userId) {
        User user = userRepository.getOrThrowForDelete(userId);

        UUID profileId = user.getProfile() != null ? user.getProfile().getId() : null;
        if (profileId != null) {
            binaryContentRepository.deleteById(profileId);
            binaryContentStorage.delete(profileId);
        }

        userRepository.delete(user);
    }

    @Transactional
    public UserDto update(UUID userId, UserUpdateRequest req, MultipartFile profile) {
        User u = userRepository.getOrThrowForUpdate(userId);

        if (req.newUsername() != null && !req.newUsername().isBlank()) {
            u.setUsername(req.newUsername().strip().toLowerCase(Locale.ROOT));
        }

        if (req.newEmail() != null && !req.newEmail().isBlank()) {
            u.setEmail(req.newEmail().strip().toLowerCase(Locale.ROOT));
        }

        if (req.newPassword() != null
            && !req.newPassword().isBlank()
            && !passwordEncoder.matches(req.newPassword(), u.getPassword())
        ) {
            u.setPassword(passwordEncoder.encode(req.newPassword()));
        }

        BinaryContent newProfile = null;
        if (profile != null && !profile.isEmpty()) {
            newProfile = binaryContentRepository.save(
                new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getSize(),
                    profile.getContentType()
                )
            );

            try {
                binaryContentStorage.put(newProfile.getId(), profile.getBytes());
            } catch (IOException e) {
                throw new UncheckedIOException("프로필 파일 저장 실패: " + newProfile.getId(), e);
            }
        }

        if (newProfile != null) {
            UUID oldProfileId = u.getProfile() != null ? u.getProfile().getId() : null;
            if (oldProfileId != null) {
                binaryContentRepository.deleteById(oldProfileId);
                binaryContentStorage.delete(oldProfileId);
            }
            u.setProfile(newProfile);
        }

        return userMapper.toDto(u);
    }

    @Transactional
    public UserStatusDto updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest req) {
        UserStatus us = userStatusRepository.getOrThrowByUserId(userId);

        if (req.newLastActiveAt() != null) {
            us.setLastActiveAt(req.newLastActiveAt());
        }

        return userStatusMapper.toDto(us);
    }
}
