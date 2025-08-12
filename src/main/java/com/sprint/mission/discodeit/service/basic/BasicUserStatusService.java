package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserService userService;
    private final UserStatusRepository userStatusRepository;

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId);
    }

    // 온라인 상태 여부
    @Override
    public boolean isOnline(UUID userId) {
        UserStatus status =  userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return status.isOnline();
    }

    // 마지막 접속 시간 업데이트
    @Override
    public void updateLastAccessedAt(UUID userId) {
        User user = userService.findById(userId);
        UserStatus userStatus = new UserStatus(user);
        userStatusRepository.save(userStatus);
    }
}
