package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    List<User> users = new ArrayList<>();

    @Override
    public User createUser(UUID id, String username, String password, int age, String email) {
        User user = new User(id, username, password, age, email);
        users.add(user);
        return user;
    }

    @Override
    public User readUser(UUID id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> readAllUsers() {
        if (!users.isEmpty()) {
            return users;
        }
        return null;
    }

    @Override
    public User updateUsername(UUID id, String username) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                user.setUsername(username);
                user.update();
                return user;
            }
        }
        return null;
    }

    @Override
    public User updatePassword(UUID id, String password) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                user.setPassword(password);
                user.update();
                return user;
            }
        }
        return null;
    }

    @Override
    public User updateEmail(UUID id, String email) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                user.setEmail(email);
                user.update();
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean deleteUser(UUID id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                users.remove(user);
                return true;
            }
        }
        return false;
    }
}
