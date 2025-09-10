package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("UserStatus already exists. userId: " + userId);
        }

        Instant lastAccessAt = request.lastActiveAt();
        UserStatus userStatus = new UserStatus(user, lastAccessAt);
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

    @Transactional
    @Override
    public UserStatus update(UUID id, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found: " + id));

        Instant newLastAccessAt = request.newLastActiveAt();
        userStatus.update(newLastAccessAt);
        return userStatusRepository.save(userStatus);
    }

    @Transactional
    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found: userId: " + userId));

        Instant newLastAccessAt = request.newLastActiveAt();
        userStatus.update(newLastAccessAt);
        return userStatus;
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found: " + id));

        userStatusRepository.delete(userStatus);
    }
}
