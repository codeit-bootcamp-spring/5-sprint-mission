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

    private final BinaryContentStorage binaryContentStorage;

    private final UserMapper userMapper;
    private final UserStatusMapper userStatusMapper;

    private final PasswordEncoder passwordEncoder;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

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

    // 락을 걸어야하나?
    // message author set null, readStatus set delete 또한 이벤트로
    @Transactional
    public void delete(UUID userId) {
        User u = userRepository.getOrThrow(userId);

        messageRepository.nullifyAuthorByUser(u);
        readStatusRepository.deleteAllByUser(u);

        userRepository.delete(u);
    }

    @Transactional
    public UserDto update(UUID userId, UserUpdateRequest req, MultipartFile profile) {
        User u = userRepository.getOrThrow(userId);

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

        if (profile != null && !profile.isEmpty()) {
            BinaryContent newProfile = binaryContentRepository.save(
                new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getSize(),
                    profile.getContentType()
                )
            );

            u.setProfile(newProfile);

            try {
                binaryContentStorage.put(newProfile.getId(), profile.getBytes());
            } catch (IOException e) {
                throw new UncheckedIOException("프로필 파일 저장 실패: " + newProfile.getId(), e);
            }
        }

        return userMapper.toDto(u);
    }

    @Transactional
    public UserStatusDto updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest req) {
        User user = userRepository.getOrThrow(userId);

        UserStatus us = user.getUserStatus();

        if (req.newLastActiveAt() != null) {
            us.setLastActiveAt(req.newLastActiveAt());
        }

        return userStatusMapper.toDto(us);
    }
}
