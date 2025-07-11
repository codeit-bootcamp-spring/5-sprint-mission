package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface UserService {
    void registerUser(User user);

    User findById(String id);

    List<User> findAll();

    void update(User user);

    void deleteById(String userId);
}
