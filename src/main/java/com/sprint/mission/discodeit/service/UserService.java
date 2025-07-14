package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void create(User user);
    User findById(UUID id);
    List<User> findAll();
    void update(UUID id, String nickName);
    void delete(String nickName);

}
