package com.sprint.mission.discodeit.service.basic;

import static org.springframework.boot.autoconfigure.container.ContainerImageMetadata.isPresent;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;
    @Transactional
    @Override
    public UserStatusResponseDto create(UUID userId, UserStatusCreateRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id " + userId));
        if(userStatusRepository.findById(userId).isPresent()){
            throw new IllegalArgumentException("UserStatus with id " + userId + " already exists");
        }
        UserStatus userStatus = new UserStatus();
        userStatus.setUser(user);
        userStatus.setLastActiveAt(request.lastActiveAt());

        UserStatus saved = userStatusRepository.save(userStatus);

        return userStatusMapper.toDto(saved);
    }

    @Override
    public UserStatusResponseDto find(UUID id) {
        UserStatus status = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with id " + id));
        return userStatusMapper.toDto(status);
    }

    @Override
    public List<UserStatusResponseDto> findAll() {
        List<UserStatus> allStatus = userStatusRepository.findAll();
        return allStatus.stream()
                .map(userStatusMapper::toDto)
                .toList();
    }

    @Override
    public UserStatusResponseDto update(UUID userStatusId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();
        UserStatus status = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with id " + userStatusId));

        status.update(newLastActiveAt);
        UserStatus updated = userStatusRepository.save(status);

        return userStatusMapper.toDto(updated);
    }

    @Override
    public UserStatusResponseDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found for user " + userId));

        status.update(newLastActiveAt);
        UserStatus updated = userStatusRepository.save(status);

        return userStatusMapper.toDto(updated);
    }

    @Override
    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new NoSuchElementException("UserStatus not found with id " + id);
        }
        userStatusRepository.deleteById(id);
    }
}
