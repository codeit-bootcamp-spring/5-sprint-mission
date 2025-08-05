package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserStatusRepository {
    void save(UserStatus userStatus);
    Optional<UserStatus> findById(UUID userId);
    void update(UUID userId);
    boolean isOnline(UUID userId);
    void delete(UUID userId);

}
