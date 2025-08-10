package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    public UserStatusResponseDto create(UserStatusCreateRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("User not found with id " + request.userId());
        }
        boolean exists = userStatusRepository.findByUserId(request.userId()).isPresent();
        if (exists) {
            throw new IllegalStateException("UserStatus already exists for user " + request.userId());
        }

        UserStatus newStatus = new UserStatus(
                UUID.randomUUID(),
                request.userId()
        );
        UserStatus saved = userStatusRepository.save(newStatus);

        return new UserStatusResponseDto(
                saved.getId(),
                saved.getUserId(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    public UserStatusResponseDto find(UUID id) {
        UserStatus status = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with id " + id));
        return new UserStatusResponseDto(
                status.getId(),
                status.getUserId(),
                status.getCreatedAt(),
                status.getUpdatedAt()
        );
    }

    public List<UserStatusResponseDto> findAll() {
        List<UserStatus> allStatus = userStatusRepository.findAll();
        return allStatus.stream()
                .map(status -> new UserStatusResponseDto(
                        status.getId(),
                        status.getUserId(),
                        status.getCreatedAt(),
                        status.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    public UserStatusResponseDto update(UserStatusUpdateRequest request) {
        UserStatus status = userStatusRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with id " + request.id()));

        status.update();
        UserStatus updated = userStatusRepository.save(status);

        return new UserStatusResponseDto(
                updated.getId(),
                updated.getUserId(),
                updated.getCreatedAt(),
                updated.getUpdatedAt()
        );
    }

    public UserStatusResponseDto updateByUserId(UserStatusUpdateByUserIdRequest request) {
        UserStatus status = userStatusRepository.findByUserId(request.userId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found for user " + request.userId()));

        status.update();
        UserStatus updated = userStatusRepository.save(status);

        return new UserStatusResponseDto(
                updated.getId(),
                updated.getUserId(),
                updated.getCreatedAt(),
                updated.getUpdatedAt()
        );
    }

    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new NoSuchElementException("UserStatus not found with id " + id);
        }
        userStatusRepository.deleteById(id);
    }
}
