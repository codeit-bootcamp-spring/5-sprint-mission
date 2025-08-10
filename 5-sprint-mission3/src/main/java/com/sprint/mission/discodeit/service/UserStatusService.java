package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("userStatusService")
public class UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserStatusService(UserStatusRepository userStatusRepository, @Qualifier("fileUserRepository") UserRepository userRepository) {
        this.userStatusRepository = userStatusRepository;
        this.userRepository = userRepository;
    }

    public UserStatus create(UserStatusRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new IllegalArgumentException(String.format("{%s} 유저가 존재하지 않습니다.", request.userId()));
        }
        List<UserStatus> userStatuses = userStatusRepository.findAll();
        userStatuses.forEach(v -> {
            if(v.getUserId().equals(request.userId())) {
                throw new IllegalArgumentException(request.userId() + "와 관련된 객체가 이미 존재합니다.");
            }
        });

        return userStatusRepository.save(new UserStatus(request.userId(), request.lastSeenAt()));
    }

    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    public UserStatus update(UserStatusRequest request) {
        UserStatus userStatus = userStatusRepository.findById(request.id()).orElseThrow(NoSuchElementException::new);
        userStatus.update(request.lastSeenAt());
        return userStatusRepository.save(userStatus);
    }

    public UserStatus updateById(UUID userId, Instant lastSeenAt) {
        List<UserStatus> userStatuses = userStatusRepository.findAll();
        for (UserStatus v : userStatuses) {
            if (v.getUserId().equals(userId)) {
                v.update(lastSeenAt);
                return  userStatusRepository.save(v);
            }
        }
        throw new IllegalArgumentException("해당 user가 존재하지 않습니다.");
    }

    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }


}
