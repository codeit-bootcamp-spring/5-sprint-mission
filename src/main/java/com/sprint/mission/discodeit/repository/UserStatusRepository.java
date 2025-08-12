package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {

    void save(UserStatus status);

    UserStatus findByUserId(String userId);

    List<UserStatus> findAll();

    void deleteByUserId(String userId);

    void delete(UUID id);

    void update(UserStatus status); // 상태 갱신

}
