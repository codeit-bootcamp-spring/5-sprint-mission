package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
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

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        UUID userId = request.userId();
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found: " + userId);
        }
        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("UserStatus already exists. userId: " + userId);
        }

        Instant lastAccessAt = request.lastAccessAt();
        UserStatus userStatus = new UserStatus(userId, lastAccessAt);
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found: " + id));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UUID id, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found: " + id));

        Instant lastAccessAt = request.lastAccessAt();
        userStatus.update(lastAccessAt);
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found: userId: " + userId));

        Instant lastAccessAt = request.lastAccessAt();
        userStatus.update(lastAccessAt);
        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID id) {
        if (userStatusRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("UserStatus not found: " + id);
        }

        userStatusRepository.delete(id);
    }
}
