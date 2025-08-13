package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service("basicUserStatusService")
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserStatus create(UUID userId, Instant lastAccessAt) {
        UserStatus userStatus = new UserStatus(userId, lastAccessAt);
        return userStatusRepository.save(userStatus);
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return userStatusRepository.findById(userId);
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public boolean isOnline(UUID userId) {
        return userStatusRepository.findById(userId).map(UserStatus::isOnline).orElse(false);
    }

    @Override
    public UserStatus update(UUID userId, Instant newLastAccessAt) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElseThrow(()
                -> new NoSuchElementException("해당하는 아이디의 유저상태를 알 수 없습니다."));
        userStatus.userAccessUpdate(newLastAccessAt);
        return userStatusRepository.save(userStatus);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        if (userStatusRepository.findByUserId(userId).isPresent()) {
            userStatusRepository.deleteById(userId);
        }
    }
}
