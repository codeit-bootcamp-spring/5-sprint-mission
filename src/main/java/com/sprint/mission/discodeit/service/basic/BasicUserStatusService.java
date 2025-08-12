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
import java.util.Optional;
import java.util.UUID;

@Service("basicUserStatusService")
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService{

    private final UserStatusRepository userStatusRepository;



    @Override
    public UserStatus create(UserStatusCreateRequest userStatusCreateRequest) {
        UUID userId = userStatusCreateRequest.userId();
        if (!userStatusRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " does not exist");
        }
        Instant lastSeenAt = userStatusCreateRequest.lastSeenAt();
        UserStatus userStatus = new UserStatus(userId, lastSeenAt);
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UUID id,  UserStatusUpdateRequest userStatusUpdateRequest) {
        Instant newLastSeenAt = userStatusUpdateRequest.newLastSeenAt();
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
        userStatus.update(newLastSeenAt);


        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID id) {
        if(!userStatusRepository.existsById(id)){
            throw new NoSuchElementException("UserStatus with id " + id + " not found");
        }
        userStatusRepository.deleteById(id);
    }
}
