package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        if (!userRepository.existsById(request.getUserId())) {
            throw new IllegalArgumentException("User not found with id: " + request.getUserId());
        }
        if (userStatusRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalArgumentException("UserStatus for user " + request.getUserId() + " already exists.");
        }

        UserStatus userStatus = UserStatus.builder()
                .userId(request.getUserId())
                .lastSeenAt(request.getLastSeenAt())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        UserStatus savedUserStatus = userStatusRepository.save(userStatus);
        return toUserStatusResponse(savedUserStatus);
    }

    @Override
    public UserStatusResponse find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
        return toUserStatusResponse(userStatus);
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::toUserStatusResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserStatusResponse update(UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + request.getId() + " not found"));

        if (request.getLastSeenAt() != null) {
            userStatus.setLastSeenAt(request.getLastSeenAt());
        }
        userStatus.setUpdatedAt(Instant.now());
        UserStatus updatedUserStatus = userStatusRepository.save(userStatus);
        return toUserStatusResponse(updatedUserStatus);
    }

    @Override
    public void updateUserStatusByUserId(UUID userId, UserStatusUpdateByUserIdRequest request) {
        UserStatus existingUserStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus for user " + userId + " not found"));

        UserStatus.UserStatusBuilder<?, ?> builder = existingUserStatus.toBuilder();

        if (request.getLastSeenAt() != null) {
            builder.lastSeenAt(request.getLastSeenAt());
        }
        builder.updatedAt(Instant.now());

        UserStatus updatedUserStatus = (UserStatus) builder.build();
        updatedUserStatus.setId(existingUserStatus.getId()); // Ensure ID remains the same

        userStatusRepository.save(updatedUserStatus);
    }

    @Override
    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new NoSuchElementException("UserStatus with id " + id + " not found");
        }
        userStatusRepository.deleteById(id);
    }

    @Override
    public void clear() {
        userStatusRepository.clear();
    }

    private UserStatusResponse toUserStatusResponse(UserStatus userStatus) {
        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getLastSeenAt(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt()
        );
    }
}