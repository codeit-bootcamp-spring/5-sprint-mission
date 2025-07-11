package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final HashMap<UUID, User> data;

    public JCFUserService() {
        data = new HashMap<>();
    }

    @Override
    public void addUser(User user) {
        data.put(user.getId(), user);
    }

    @Override
    public void updateUser(User user) {
        data.put(user.getId(), user);
    }

    @Override
    public void deleteUser(UUID id) {
        data.remove(id);
    }

    @Override
    public User getUser(UUID id) {
        return data.get(id);
    }

    @Override
    public HashMap<UUID, User> getAllUsers() {
        return data;
    }
}
