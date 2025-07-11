package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public void create(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User findById(UUID id) {
        if  (users.containsKey(id)) {
            return users.get(id);
        }

        return null;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void update(UUID id, String name, int age) {
        User user = users.get(id);
        if (user != null) {
            user.update(name, age);
        }
    }

    @Override
    public void delete(UUID id) {
        users.remove(id);
    }

    @Override
    public String toString() {
        return "JCFUserService{" +
                "users=" + users +
                '}';
    }
}
