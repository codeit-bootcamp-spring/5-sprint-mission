package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class JCFUserService implements UserService {

    private final List<User> data;

    public JCFUserService() {
        data = new ArrayList<>();
    }

    @Override
    public void addUser(User user) {
        data.add(user);
    }

    @Override
    public void updateUser(User user) {
        int i = data.indexOf(user);
        data.set(i, user);
    }

    @Override
    public void deleteUser(User user) {
        data.remove(user);
    }

    @Override
    public User getUser(int i) {
        return data.get(i);
    }

    @Override
    public List<User> getAllUsers() {
        return data;
    }
}
