package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
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
    public UserStatus create(UserStatusCreateRequest request) {
        UUID userId = request.userId();
        if(!userRepository.existsById(userId)){
            //값이 존재하지 않을 때
            throw new NoSuchElementException("User with id " + userId + " does not exist");
        }
        if(userStatusRepository.findByUserId(userId).isPresent()){
            //값이 이미 존재할 때 (일부러 예외로)
            throw new IllegalArgumentException("UserStatus with id " + userId + " already exists");
        }
        Instant lastActiveAt = request.lastActiveAt();
        UserStatus userStatus = new UserStatus(userId, lastActiveAt);
        return userStatusRepository.save(userStatus);

    }

    @Override
    public UserStatus find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
    }
    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll().stream()
                .toList();
    }

    @Override
    public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();
        UserStatus userStatus= userStatusRepository.findById(userStatusId)
                .orElseThrow(()-> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
        userStatus.update(newLastActiveAt);

        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();
        UserStatus userStatus= userStatusRepository.findById(userId)
                .orElseThrow(()-> new NoSuchElementException("UserStatus with id " + userId + " not found"));
        userStatus.update(newLastActiveAt);

        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(()-> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));

        userStatusRepository.deleteById(userStatusId);

    }
}
