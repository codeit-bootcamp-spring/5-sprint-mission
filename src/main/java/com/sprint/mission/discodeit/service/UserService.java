package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    void create(User user);
    User get(UUID id);
    User get(String name);
    List<User> getAll();
    void update(User user);
    void delete(UUID id);
}
