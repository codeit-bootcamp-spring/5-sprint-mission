package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponseDto create(UserStatusCreateRequest request) {
        UUID userId = UUID.randomUUID();

        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found with id " + userId);
        }
        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("UserStatus with id " + userId + " already exists");
        }
        UserStatus userStatus = new UserStatus(userId, request.lastActiveAt());

        UserStatus saved = userStatusRepository.save(userStatus);

        return UserStatusResponseDto.fromEntity(saved);
    }

    @Override
    public UserStatusResponseDto find(UUID id) {
        UserStatus status = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with id " + id));
        return UserStatusResponseDto.fromEntity(status);
    }

    @Override
    public List<UserStatusResponseDto> findAll() {
        List<UserStatus> allStatus = userStatusRepository.findAll();
        return allStatus.stream()
                .map(UserStatusResponseDto::fromEntity)
                .toList();
    }

    @Override
    public UserStatusResponseDto update(UUID userStatusId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();
        UserStatus status = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with id " + userStatusId));

        status.update(newLastActiveAt);
        UserStatus updated = userStatusRepository.save(status);

        return UserStatusResponseDto.fromEntity(updated);
    }

    @Override
    public UserStatusResponseDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();

        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // UserStatus 없으면 새로 생성
                    return new UserStatus(userId, newLastActiveAt);
                });

        // 업데이트
        status.update(newLastActiveAt);

        UserStatus updated = userStatusRepository.save(status);

        return UserStatusResponseDto.fromEntity(updated);
    }


    @Override
    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new NoSuchElementException("UserStatus not found with id " + id);
        }
        userStatusRepository.deleteById(id);
    }
}
