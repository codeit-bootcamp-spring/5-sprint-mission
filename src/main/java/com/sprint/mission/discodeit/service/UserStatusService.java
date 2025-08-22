package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import java.util.UUID;

//유저 상태
public interface UserStatusService {

  //상태 업데이트
  void updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest request);
}
