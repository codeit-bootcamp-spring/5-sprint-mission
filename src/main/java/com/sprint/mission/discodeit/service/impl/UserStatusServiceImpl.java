package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserStatusServiceImpl implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  //유저 상태 업데이트
  @Override
  @Transactional
  public void updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

    UserStatus status = userStatusRepository.findByUser_Id(userId).orElse(null);

    if (status == null) {
      UserStatus newStatus = new UserStatus(user, request.getNewLastActiveAt());
      userStatusRepository.save(newStatus);
    } else {

      /* 요청으로 들어온 시간 있으면, status의 lastActive 값 바꿈
       *save 안써도 자동 update
       */
      if (request.getNewLastActiveAt() != null) {
        status.setLastActiveAt(request.getNewLastActiveAt());
      }
    }
  }
}