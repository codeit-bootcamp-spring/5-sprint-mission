package com.codeit.mission.discodeit.service.basic;

import com.codeit.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.codeit.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.codeit.mission.discodeit.dto.userstatus.UserStatusUpdateByUserRequest;
import com.codeit.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.codeit.mission.discodeit.entity.UserStatus;
import com.codeit.mission.discodeit.repository.UserRepository;
import com.codeit.mission.discodeit.repository.UserStatusRepository;
import com.codeit.mission.discodeit.service.UserStatusService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("basicUserStatusService")
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    public BasicUserStatusService(@Qualifier("userStatusRepository") UserStatusRepository userStatusRepository,
                                  @Qualifier("userRepository") UserRepository userRepository) {
        this.userStatusRepository = userStatusRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        if (!userRepository.existsById(request.getUserId())) {
            throw new NoSuchElementException("User not found");
        }

        boolean alreadyExists = userStatusRepository.findAll().stream()
                .anyMatch(userStatus -> userStatus.getUserId().equals(request.getUserId()));

        if (alreadyExists) {
            throw new IllegalArgumentException("UserStatus already exists for userId");
        }

        UserStatus userStatus = new UserStatus(request.getUserId(), request.getLastAccessTime());
        UserStatus savedUserStatus = userStatusRepository.save(userStatus);

        return new UserStatusResponse(savedUserStatus);
    }

    @Override
    public UserStatusResponse find(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found"));

        return new UserStatusResponse(userStatus);
    }

    @Override
    public List<UserStatusResponse> findAll() {
        List<UserStatus> userStatuses = userStatusRepository.findAll();

        return userStatuses.stream()
                .map(UserStatusResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public UserStatusResponse update(UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(request.getUserStatusId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found"));

        userStatus.update(request.getLastAccessTime());
        UserStatus savedUserStatus = userStatusRepository.save(userStatus);

        return new UserStatusResponse(savedUserStatus);
    }

    @Override
    public UserStatusResponse updateByUserId(UserStatusUpdateByUserRequest request) {
        UserStatus userStatus = userStatusRepository.findAll().stream()
                .filter(status -> status.getUserId().equals(request.getUserId()))
                .findFirst()
                .orElse(null);

        if (userStatus == null) {
            throw new NoSuchElementException("UserStatus not found for userId");
        }

        userStatus.update(request.getLastAccessTime());
        UserStatus savedUserStatus = userStatusRepository.save(userStatus);

        return new UserStatusResponse(savedUserStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        if (!userStatusRepository.existsById(userStatusId)) {
            throw new NoSuchElementException("UserStatus not found");
        }

        userStatusRepository.deleteById(userStatusId);
    }
}