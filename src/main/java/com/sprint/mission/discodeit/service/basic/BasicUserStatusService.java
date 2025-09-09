package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserStatus create(UserStatusCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (userStatusRepository.findByUser(user).isPresent()) {
            throw new IllegalArgumentException("UserStatus for this user already exists");
        }

        UserStatus userStatus = new UserStatus(user, request.lastActiveAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatus find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new EntityNotFoundException("UserStatus not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    @Transactional
    public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
        UserStatus userStatus = find(userStatusId);

        userStatus.update(request.newLastActiveAt());

        return userStatus;
    }

    @Override
    @Transactional
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserStatus userStatus = userStatusRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("UserStatus not found for this user"));

        userStatus.update(request.newLastActiveAt());

        return userStatus;
    }

    @Override
    @Transactional
    public void delete(UUID userStatusId) {
        if (!userStatusRepository.existsById(userStatusId)) {
            throw new EntityNotFoundException("UserStatus not found");
        }
        userStatusRepository.deleteById(userStatusId);
    }
}
