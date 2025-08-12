package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service("BasicUserStatusService")
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        if (!userRepository.existsById(request.getUserId())) {
            throw new NoSuchElementException("User not found with id: " + request.getUserId());
        }

        if (userStatusRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalStateException("UserStatus already exists for userId: " + request.getUserId());
        }

        final UserStatus userStatus = new UserStatus(request.getUserId(), request.getLastOnlineAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with id: " + id));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UserStatusUpdateRequest request) {
        final UserStatus userStatus = userStatusRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with id: " + request.getId()));

        userStatus.update(request.getLastOnlineAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId) {
        final UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found for userId: " + userId));

        userStatus.update();
        return userStatusRepository.save(userStatus);
    }

    @Override
    public boolean delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            return false;
        }
        return userStatusRepository.deleteById(id);
    }
}
