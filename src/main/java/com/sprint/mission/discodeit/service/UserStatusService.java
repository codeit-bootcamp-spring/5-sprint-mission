package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserStatusService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    public UserStatus create(UserStatusCreateRequest request) {
        userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("create : 유저를 찾을 수 없습니다."));
        UserStatus userStatus = userStatusRepository.findAll().stream()
                .filter(s -> s.getUserId().equals(request.userId()))
                .findFirst()
                .orElse(null);
        if (userStatus != null) {
            throw new IllegalArgumentException("create : UserStatus가 이미 존재합니다.");
        }
        userStatus = new UserStatus(request.userId());
        return userStatusRepository.save(userStatus);
    }

    public UserStatus findById(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("findById : UserStatus를 찾을 수 없습니다."));
    }

    public UserStatus findByUserId(UUID userid) {
        return userStatusRepository.findByUserId(userid)
                .orElseThrow(() -> new NoSuchElementException("findByUserId : UserStatus를 찾을 수 없습니다."));
    }

    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    public UserStatus update(UserStatusUpdateRequest userStatusUpdateRequest) {
        UserStatus userStatus = userStatusRepository.findById(userStatusUpdateRequest.id())
                .orElseThrow(() -> new NoSuchElementException("update : UserStatus를 찾을 수 없습니다."));
        userStatus.update(userStatusUpdateRequest.loginStatus());

        return userStatusRepository.save(userStatus);
    }

    public UserStatus updateByUserId(UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(request.id())
                .orElseThrow(() -> new NoSuchElementException("updateByUserId : UserStatus를 찾을 수 없습니다."));
        userStatus.update(request.loginStatus());

        return userStatusRepository.save(userStatus);
    }

    public void delete(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("delete : UserStatus를 찾을 수 없습니다."));
        userStatusRepository.deleteById(userStatus.getId());
    }

    public void deleteAll() {
        userStatusRepository.findAll().forEach(userStatus -> delete(userStatus.getId()));
    }
}
