package com.codeit.mission.discodeit.service.basic;

import com.codeit.mission.discodeit.dto.user.ProfileImageRequest;
import com.codeit.mission.discodeit.dto.user.UserCreateRequest;
import com.codeit.mission.discodeit.dto.user.UserResponse;
import com.codeit.mission.discodeit.dto.user.UserUpdateRequest;
import com.codeit.mission.discodeit.entity.BinaryContent;
import com.codeit.mission.discodeit.entity.User;
import com.codeit.mission.discodeit.entity.UserStatus;
import com.codeit.mission.discodeit.repository.BinaryContentRepository;
import com.codeit.mission.discodeit.repository.UserRepository;
import com.codeit.mission.discodeit.repository.UserStatusRepository;
import com.codeit.mission.discodeit.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("basicUserService")
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    public BasicUserService(@Qualifier("userRepository") UserRepository userRepository,
                            @Qualifier("userStatusRepository") UserStatusRepository userStatusRepository,
                            @Qualifier("binaryContentRepository") BinaryContentRepository binaryContentRepository) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public UserResponse create(UserCreateRequest request) {
        if (isDuplicateUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (isDuplicateEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User(request.getUsername(), request.getEmail(), request.getPassword());
        User savedUser = userRepository.save(user);

        UserStatus userStatus = new UserStatus(savedUser.getId(), Instant.now());
        UserStatus savedUserStatus = userStatusRepository.save(userStatus);

        UUID profileImageId = null;
        if (request.getProfileImage() != null) {
            profileImageId = saveProfileImage(request.getProfileImage(), savedUser.getId());
        }

        return new UserResponse(savedUser, savedUserStatus, profileImageId);
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        UserStatus userStatus = userStatusRepository.findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
        UUID profileImageId = findUserProfileImageId(userId);

        return new UserResponse(user, userStatus, profileImageId);
    }

    @Override
    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> {
                    UserStatus userStatus = userStatusRepository.findAll().stream()
                            .filter(status -> status.getUserId().equals(user.getId()))
                            .findFirst()
                            .orElse(null);
                    UUID profileImageId = findUserProfileImageId(user.getId());
                    return new UserResponse(user, userStatus, profileImageId);
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (isDuplicateUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (isDuplicateEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
        }

        user.update(request.getUsername(), request.getEmail(), request.getPassword());
        User savedUser = userRepository.save(user);

        UUID profileImageId = null;
        if (request.getProfileImage() != null) {
            deleteExistingProfileImage(request.getId());
            profileImageId = saveProfileImage(request.getProfileImage(), request.getId());
        } else {
            profileImageId = findUserProfileImageId(request.getId());
        }

        UserStatus userStatus = userStatusRepository.findAll().stream()
                .filter(status -> status.getUserId().equals(request.getId()))
                .findFirst()
                .orElse(null);
        return new UserResponse(savedUser, userStatus, profileImageId);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found");
        }

        deleteExistingProfileImage(userId);
        deleteUserStatusByUserId(userId);
        userRepository.deleteById(userId);
    }

    private boolean isDuplicateUsername(String username) {
        return userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    private boolean isDuplicateEmail(String email) {
        return userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    private UUID saveProfileImage(ProfileImageRequest profileImageRequest, UUID userId) {
        BinaryContent binaryContent = new BinaryContent(
                profileImageRequest.getFileName(),
                profileImageRequest.getContentType(),
                profileImageRequest.getSize(),
                profileImageRequest.getBytes(),
                userId, null
        );

        BinaryContent savedContent = binaryContentRepository.save(binaryContent);
        return savedContent.getId();
    }

    private UUID findUserProfileImageId(UUID userId) {
        return binaryContentRepository.findAll().stream()
                .filter(content -> userId.equals(content.getProfileId()))
                .findFirst()
                .map(BinaryContent::getId)
                .orElse(null);
    }

    private void deleteExistingProfileImage(UUID userId) {
        binaryContentRepository.findAll().stream()
                .filter(content -> userId.equals(content.getProfileId()))
                .findFirst()
                .ifPresent(content -> binaryContentRepository.deleteById(content.getId()));
    }

    private void deleteUserStatusByUserId(UUID userId) {
        Optional<UserStatus> userStatusOpt = userStatusRepository.findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();

        if (userStatusOpt.isPresent()) {
            UserStatus userStatus = userStatusOpt.get();
            UUID userStatusId = userStatus.getId();

            userStatusRepository.deleteById(userStatusId);
        }
    }
}
