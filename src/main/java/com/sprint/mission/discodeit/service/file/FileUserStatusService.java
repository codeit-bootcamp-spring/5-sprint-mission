package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileUserStatusService implements UserStatusService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;

  //상태 업데이트
  @Override
  public void updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest request) {
    UserStatus status = userStatusRepository.findAll().stream()
        .filter(s -> s.getUserId().equals(userId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("해당 유저 상태 정보 없음"));

    status.setUpdatedAt(Instant.now());
    status.setLastOnline(request.getNewLastActiveAt());

    userStatusRepository.update(status);
  }
}
