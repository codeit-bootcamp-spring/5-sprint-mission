package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId);
    }

    // 마지막 접속 시간 업데이트
    @Override
    public void updateLastAccessedAt(UUID userId) {
        // 사용자 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 온라인 상태 객체 생성 후 업데이트
        UserStatus userStatus = new UserStatus(user.getId());
        userStatus.updateLastAccessedAt();

        //상태 저장
        userStatusRepository.save(userStatus);
    }

    // 온라인 상태 여부
    @Override
    public boolean isOnline(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .map(UserStatus::isOnline)
                .orElse(false); // 기록이 없다면 오프라인
    }
}
