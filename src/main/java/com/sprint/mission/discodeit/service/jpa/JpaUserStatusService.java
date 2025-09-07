package com.sprint.mission.discodeit.service.jpa;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class JpaUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (userStatusRepository.findByUserId(user.getId()).isPresent()) {
            throw new IllegalArgumentException("UserStatus already exists");
        }

        UserStatus userStatus = new UserStatus(user, request.lastActiveAt());
        return new UserStatusResponse(userStatusRepository.save(userStatus));
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatusResponse find(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> new NoSuchElementException("UserStatus not found"));
        return new UserStatusResponse(userStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
            .map(UserStatusResponse::new)
            .toList();
    }

    @Override
    public UserStatusResponse update(UUID userStatusId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> new NoSuchElementException("UserStatus not found"));

        userStatus.update(request.newLastActiveAt());
        return new UserStatusResponse(userStatus);
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> new NoSuchElementException("UserStatus not found"));

        userStatus.update(request.newLastActiveAt());
        return new UserStatusResponse(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> new NoSuchElementException("UserStatus not found"));
        userStatusRepository.delete(userStatus);
    }
}
