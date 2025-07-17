package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserSerivce implements UserService {

    final Map<UUID, User> data = new HashMap<>();

    @Override
    public User createUser(String username) {
        User user = new User(username);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(UUID userId) {
        if(!data.containsKey(userId)){
            throw new NoSuchElementException("user not found");
        }
        return data.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User updateUser(UUID userId, String username) {
        User user = getUser(userId);
        user.update(username);
        return user;
    }

    @Override
    public void deleteUser(UUID userId) {
        if(!data.containsKey(userId)){
            throw new NoSuchElementException("user not found");
        }
        data.remove(userId);
    }
}
