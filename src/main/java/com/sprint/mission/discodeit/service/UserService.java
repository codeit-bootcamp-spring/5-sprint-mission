package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void add(User user);
    User findOne(UUID userId);
    List<User> findAll();
    void update(UUID originUserUuid , User newUser);
    void delete(UUID userId);
    void deleteAll();

}
