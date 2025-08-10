package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JCFUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username can't be null or blank");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password can't be null or blank");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with username '" + request.getUsername() + "' already exists.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .profileImageId(request.getProfileImageId())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        User savedUser = userRepository.save(user);

        UserStatus userStatus = UserStatus.builder()
                .userId(savedUser.getId())
                .lastSeenAt(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        userStatusRepository.save(userStatus);

        return toUserResponse(savedUser, userStatus);
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElse(null);
        return toUserResponse(user, userStatus);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                            .orElse(null);
                    return toUserResponse(user, userStatus);
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + request.getId() + " not found"));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword());
        }
        if (request.getProfileImageId() != null) {
            user.setProfileImageId(request.getProfileImageId());
        }
        user.setUpdatedAt(Instant.now());
        User updatedUser = userRepository.save(user);

        UserStatus userStatus = userStatusRepository.findByUserId(updatedUser.getId())
                .orElse(null);

        return toUserResponse(updatedUser, userStatus);
    }

    @Override
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
        userStatusRepository.findByUserId(id).ifPresent(userStatus -> userStatusRepository.deleteById(userStatus.getId()));
        // TODO: Delete profile image if exists
    }

    @Override
    public Optional<UserResponse> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                            .orElse(null);
                    return toUserResponse(user, userStatus);
                });
    }

    @Override
    public void clear() {
        userRepository.clear();
        userStatusRepository.clear();
        binaryContentRepository.clear(); // Assuming binary content is cleared with users
    }

    private UserResponse toUserResponse(User user, UserStatus userStatus) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getProfileImageId(),
                userStatus != null && userStatus.isOnline()
        );
    }
}
