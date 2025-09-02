package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.status.user.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.status.user.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.status.user.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ConflictException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(CreateUserStatusRequest request) {
        if (userRepository.findById(request.userId()).isEmpty()) throw new NotFoundException("User not found: " + request.userId());
        if (userStatusRepository.existsByUserId(request.userId())) throw new ConflictException("UserStatus already exists: " + request.userId());

        UserStatus status = new UserStatus(request.userId());
        return toResponse(status);
    }

    @Override
    public Optional<UserStatusResponse> findById(UUID id) {
        return userStatusRepository.findById(id).map(this::toResponse);
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserStatusResponse update(UpdateUserStatusRequest request) {
        UserStatus status = userStatusRepository.findById(request.id())
                .orElseThrow(() -> new NotFoundException("User not found: " + request.id()));

        status.isOnline();
        return toResponse(status);
    }

    @Override
    public UserStatusResponse updateByUserId(UpdateUserStatusByUserIdRequest request) {
        UserStatus status = userStatusRepository.findByUserId(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found: " + request.userId()));

        status.isOnline();
        return toResponse(status);
    }

    @Override
    public boolean remove(UUID id) {
        return userStatusRepository.delete(id);
    }

    private UserStatusResponse toResponse(UserStatus status) {
        return null;
    }
}
