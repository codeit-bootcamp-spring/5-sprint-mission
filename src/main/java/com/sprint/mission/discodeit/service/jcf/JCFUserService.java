package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userMap;

    public JCFUserService() {
        userMap = new HashMap<>();
    }

    @Override
    public User create(String name, String email, String password) {
        if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("User info is invalid");
        }
        User user = new User(name, email, password);
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User find(UUID userId) {
        User user = userMap.get(userId);
        if (user == null) {
            throw new NoSuchElementException("User not found");
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User update(UUID userId, String name, String email, String password) {
        User user = userMap.get(userId);
        if (user == null) {
            throw new NoSuchElementException("User not found");
        }
        user.update(name, email, password);
        return user;
    }

    @Override
    public boolean delete(UUID userId) {
        return userMap.remove(userId, find(userId));
    }
}
