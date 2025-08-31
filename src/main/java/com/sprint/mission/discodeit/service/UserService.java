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
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    private final UserMapper userMapper;
    private final UserStatusMapper userStatusMapper;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto create(UserCreateRequest req, MultipartFile profile) {
        String username = req.username().strip().toLowerCase(Locale.ROOT);
        String email = req.email().strip().toLowerCase(Locale.ROOT);
        String password = passwordEncoder.encode(req.password());
        BinaryContent savedProfile = (profile != null && !profile.isEmpty())
            ? binaryContentRepository.save(toBinaryContentFromMultipartFile(profile))
            : null;

        User user = userRepository.save(
            new User(
                username,
                email,
                password,
                savedProfile,
                null
            )
        );

        user.setUserStatus(userStatusRepository.save(new UserStatus(user)));

        return userMapper.toDto(user);
    }

    @Transactional
    public void delete(UUID userId) {
        User user = userRepository.getOrThrowForDelete(userId);

        if (user.getProfile() != null) {
            binaryContentRepository.deleteById(user.getProfile().getId());
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

        BinaryContent newProfile = (profile != null && !profile.isEmpty())
            ? binaryContentRepository.save(toBinaryContentFromMultipartFile(profile))
            : null;

        if (newProfile != null) {
            UUID oldProfileId = u.getProfile() != null ? u.getProfile().getId() : null;
            u.setProfile(newProfile);
            if (oldProfileId != null) {
                binaryContentRepository.deleteById(oldProfileId);
            }
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
