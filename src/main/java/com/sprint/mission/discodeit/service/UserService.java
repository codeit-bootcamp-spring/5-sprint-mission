package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User save(User userDto);

    User findById(UUID id);

    List<User> findAll();

    User update(UUID id, User userDto);

    void delete(UUID id);
}
