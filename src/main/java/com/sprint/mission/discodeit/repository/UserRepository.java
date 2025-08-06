package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.Map;
import java.util.UUID;

public interface UserRepository {
    void save(Map<UUID, User> data);
    Map<UUID, User> loadData();
    void clear();


}
