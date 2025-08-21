package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatus getUserStatusById(UUID id);

  UserStatus addUserStatus(UUID userId);

  List<UserStatus> getAllUserStatus();

  void deleteAllUserStatus();

  UserStatus updateUserStatus(UUID id);

  UserStatus updateUserStatusByUserId(UUID userId);

  void deleteUserStatus(UUID userStatusId);


}

