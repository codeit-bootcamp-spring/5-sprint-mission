package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
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
    private final UserStatusMapper userStatusMapper;

    @Transactional
    @Override
    public UserStatusDto create(UserStatusCreateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("UserStatus already exists. userId: " + userId);
        }

        Instant lastAccessAt = request.lastActiveAt();
        UserStatus userStatus = new UserStatus(user, lastAccessAt);
        return userStatusMapper.toDto(userStatusRepository.save(userStatus));
    }

    @Override
    public UserStatusDto find(UUID id) {
        return userStatusRepository.findById(id)
                .map(userStatusMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found: " + id));
    }

    @Override
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public UserStatusDto update(UUID id, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found: " + id));

        Instant newLastAccessAt = request.newLastActiveAt();
        userStatus.update(newLastAccessAt);
        return userStatusMapper.toDto(userStatusRepository.save(userStatus));
    }

    @Transactional
    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found: userId: " + userId));

        Instant newLastAccessAt = request.newLastActiveAt();
        userStatus.update(newLastAccessAt);
        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new NoSuchElementException("UserStatus not found: " + id);
        }

        userStatusRepository.deleteById(id);
    }
}
