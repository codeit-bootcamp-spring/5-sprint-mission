package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
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
    public UserStatusResponse create(UserStatusCreateRequest request) {
        if (!userRepository.existsById(request.getUserId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }
        if (userStatusRepository.findById(request.getUserId()).isPresent()) {
            throw new RuntimeException("이미 존재하는 UserStatus 입니다.");
        }
        UserStatus userStatus = new UserStatus(request.getUserId(), Instant.now());
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public UserStatusResponse findById(UUID id) {
        UserStatus status = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다."));
        return toResponse(status);
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserStatusResponse update(UserStatusUpdateRequest request) {
        UserStatus status = userStatusRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다."));

        status.updateLastOnlineTime(request.getLastOnlineTime());
        userStatusRepository.save(status);

        return toResponse(status);
    }

    @Override
    public UserStatusResponse updateByUserId(UserStatusUpdateByUserIdRequest request) {
        UserStatus status = userStatusRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다."));

        status.updateLastOnlineTime(request.getLastOnlineTime());
        userStatusRepository.save(status);

        return toResponse(status);
    }

    @Override
    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new NoSuchElementException("UserStatus를 찾을 수 없습니다.");
        }
        userStatusRepository.deleteById(id);
    }

    private UserStatusResponse toResponse(UserStatus status) {
        return new UserStatusResponse(
                status.getId(),
                status.getUserId(),
                status.getLastOnlineTime(),
                status.isOnline()
        );
    }
}
