package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {
    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public User createUser(String username, String password, int age, String email) {
        User user = new User(username, password, age, email);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User readUser(UUID id) {
        return users.get(id);
    }

    @Override
    public List<User> readAllUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public User updateUsername(UUID id, String username) {
        User user = users.get(id);
        if (user != null) {
            user.setUsername(username);
        }
        return user;
    }

    @Override
    public User updatePassword(UUID id, String password) {
        User user = users.get(id);
        if (user != null) {
            user.setPassword(password);
        }
        return user;
    }

    @Override
    public User updateEmail(UUID id, String email) {
        User user = users.get(id);
        if (user != null) {
            user.setEmail(email);
        }
        return user;
    }

    @Override
    public boolean deleteUser(UUID id) {
        if (users.containsKey(id)) {
            users.remove(id);
            return true;
        }
        return false;
    }
}
