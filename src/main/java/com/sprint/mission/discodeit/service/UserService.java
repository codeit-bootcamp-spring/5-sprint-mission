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

        User savedUser = userRepository.save(
            new User(
                username,
                email,
                password,
                savedProfile
            )
        );

        UserStatus savedUserStatus = userStatusRepository.save(new UserStatus(savedUser));

        return userMapper.toDto(savedUser, savedUserStatus);
    }

    // delete 중에 해당 userId를 다른 트랜잭션에서 사용할 경우 nullify/delete 되지 않은 row가 남아 있을 수 있음
    // fk 제약을 걸면 해결되나 soft delete 정책으로 바뀌면 문제는 다시 발생한다.
    // message author id를 그냥 남겨두고 사용할 수 있지 않을까?
    // 고아 객체를 삭제하는 워커를 쓰는게 낫지 않을까?
    @Transactional
    public void delete(UUID userId) {
        User user = userRepository.getOrThrow(userId);

        UUID profileId;
        if (user.getProfile() != null) {
            profileId = user.getProfile().getId();
            user.setProfile(null);
        } else {
            profileId = null;
        }

        messageRepository.nullifyAllAuthorByUserId(userId);

        readStatusRepository.deleteAllByUserId(userId);

        userStatusRepository.deleteAllByUserId(userId);

        if (profileId != null) {
            binaryContentRepository.deleteById(profileId);
        }

        userRepository.delete(user);
    }

    @Transactional
    public UserDto update(UUID userId, UserUpdateRequest req, MultipartFile profile) {
        User u = userRepository.getOrThrow(userId);

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
                binaryContentRepository.deleteById(oldProfileId);
            }
        }

        return userMapper.toDto(u, userStatusRepository.getOrCreateByUser(u));
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
