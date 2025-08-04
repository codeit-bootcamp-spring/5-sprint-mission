package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.respository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;

    // 온라인 상태 여부
    @Override
    public boolean isOnline(UUID userId) {
        UserStatus status =  userStatusRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return status.isOnline();
    }

    // 마지막 접속 시간 업데이트
    @Override
    public void updateLastAccessedAt(User user) {
        // 기존 UserStatus 조회 (없으면 새로 생성)
         UserStatus status = userStatusRepository.findById(user.getId())
                .orElseGet(() -> new UserStatus(user));

        status.updateLastAccessedAt(); // 마지막 접속시간 갱신
        userStatusRepository.save(status); // 저장
    }
}
