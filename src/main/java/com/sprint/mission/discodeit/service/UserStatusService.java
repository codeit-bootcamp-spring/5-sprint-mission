package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.UUID;

//유저 상태
public interface UserStatusService {

  //상태 업데이트
  void updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest request);

  //단건 조회
  UserStatus findByUserId(UUID userId);

  //상태 삭제
  void delete(UUID userId);

  //모든 상태 조회
  List<UserStatus> findAll();
}
