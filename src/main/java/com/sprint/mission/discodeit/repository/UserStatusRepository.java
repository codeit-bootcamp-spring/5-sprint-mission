package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {


  void update(UserStatus status);

  UserStatus findByUserId(String userId);

  List<UserStatus> findAll();

  void save(UserStatus status);

  void delete(UUID id);
}
