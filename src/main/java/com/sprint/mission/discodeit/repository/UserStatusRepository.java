package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {

  void save(UserStatus status);

  UserStatus findByUserId(String userId);


  void delete(UUID userId);

  void update(UserStatus status);

  List<UserStatus> findAll();
}
