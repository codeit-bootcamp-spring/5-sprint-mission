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
    public User findById(UUID id) {
        return null;
    }

    @Override
    public User findByName(String nickName) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void update(UUID id, String nickName) {

    }

    @Override
    public void delete(UUID id) {

    }
}
