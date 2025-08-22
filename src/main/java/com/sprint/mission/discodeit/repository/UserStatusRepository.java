package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;

public interface UserStatusRepository {


  void update(UserStatus status);

  UserStatus findByUserId(String userId);

  List<UserStatus> findAll();
}
