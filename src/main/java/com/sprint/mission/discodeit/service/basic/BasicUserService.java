package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
    @Override
    public void create(User user) {

    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(User user) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public User searchById(UUID id) {
        return null;
    }

    @Override
    public List<User> searchByName(String name) {
        return List.of();
    }

    @Override
    public List<User> searchAll() {
        return List.of();
    }
}
