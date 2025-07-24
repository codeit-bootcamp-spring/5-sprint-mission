package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final List<User> data;

    public JCFUserService() {
        data = new ArrayList<>();
    }

    @Override
    public void create(User user) {
        data.add(user);
    }

    @Override
    public void update(User user) {
        int i = data.indexOf(user);
        data.set(i, user);
    }

    @Override
    public void delete(User user) {
        data.remove(user);
    }

    @Override
    public void deleteAll() {
        data.clear();
    }

    @Override
    public User searchByIndex(int i) {
        return data.get(i);
    }

    @Override
    public User searchById(UUID id) {
        for (User user : data) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> searchByName(String name) {
        List<User> users = new ArrayList<>();

        for (User user : data) {
            if (user.getName().contains(name)) {
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public List<User> searchAll() {
        return data;
    }

    @Override
    public String toString() {
        return "JCFUserService{" +
                "data=" + data +
                '}';
    }
}
