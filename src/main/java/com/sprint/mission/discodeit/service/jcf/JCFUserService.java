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
    public User create(String userId, String name, String password) {
        User user = new User(userId, password, name);
        data.add(user);
        return user;
    }

    @Override
    public User get(String userId) {
        for (User user : data) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(data);
    }

    @Override
    public boolean updateUserName(String userId, String name) {
        for (User user : data) {
            if (user.getUserId().equals(userId)) {
                user.setName(name);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        for (User user : data) {
            if (user.getUserId().equals(userId) && user.getPassword().equals(oldPassword)) {
                user.setPassword(newPassword);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(String userId) {
        for (User user : data) {
            if (user.getUserId().equals(userId)) {
                data.remove(user);
                return  true;
            }
        }
        return false;
    }
}
