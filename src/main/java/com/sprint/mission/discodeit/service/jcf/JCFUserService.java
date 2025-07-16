package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    final Map<UUID, User> data = new HashMap<>();

    @Override
    public User create(String username, String password) {
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("username or password is null or blank");
        }

        User user = new User(username, password);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User find(UUID userId) {
        if (!data.containsKey(userId)) {
            throw new NoSuchElementException("user not found");
        }
        return data.get(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID userId, String username, String password) {
        User user = data.get(userId);

        if (user == null) {
            throw new NoSuchElementException("user not found");
        }

        user.update(username, password);
        return user;
    }

    @Override
    public void delete(UUID userId) {
        data.remove(userId);
    }
}
