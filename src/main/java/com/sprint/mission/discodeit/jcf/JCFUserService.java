package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> users = new ArrayList<>();

    @Override
    public boolean register(User user) {
        if (user.getName() == null || user.getPassword() == null || user.getName().isBlank() || user.getPassword().isBlank()) {
            return false;
        }
        users.add(user);
        return true;
    }

    @Override
    public User findById(UUID id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(users);
    }

    @Override
    public boolean update(UUID id, String newPW) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                user.setPassword(newPW);
                user.setUpdateAt(System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(UUID id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                users.remove(user);
                return true;
            }
        }
        return false;
    }
}
