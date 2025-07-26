package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;

    public JCFUserService() {
        data = new HashMap<>();
    }

    @Override
    public User create(User user) {
        return data.put(user.getId(), user);
    }

    @Override
    public User update(User user) {
        return data.put(user.getId(), user);
    }

    @Override
    public User delete(UUID id) {
        return data.remove(id);
    }

    @Override
    public void deleteAll() {
        data.clear();
    }

    @Override
    public Optional<User> searchById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> searchByName(String name) {
        List<User> users = new ArrayList<>();

        for (User user : data.values()) {
            if (user.getName().contains(name)) {
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public List<User> searchAll() {
        return new ArrayList<>(data.values());
    }
}
