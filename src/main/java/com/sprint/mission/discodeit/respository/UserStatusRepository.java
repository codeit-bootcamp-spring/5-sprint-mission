package com.sprint.mission.discodeit.respository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {

    void save(UserStatus userStatus);

    List<UserStatus> findAll();

    Optional<UserStatus> findById(UUID userId);



}
