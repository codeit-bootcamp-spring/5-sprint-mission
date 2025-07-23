package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private static final List<User> data = new ArrayList<>();;
    private static JCFUserService instance;

    private JCFUserService() {}

    public static JCFUserService getInstance() {
        if (instance == null) {
            instance = new JCFUserService();
        }

        return instance;
    }

    @Override
    public void addUser(User user) {
        if(user == null){
            return;
        }

        data.add(user);
    }

    @Override
    public List<User> getUsers() {
        return data;
    }

    @Override
    public User getUserById(UUID id) {
        return data.stream().filter(u->u.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public User getUserByUsername(String username) {
        return data.stream().filter(u->
                u.getUserName()
                    .equals(username))
                    .findFirst()
                    .orElse(null);

    }

    @Override
    public void updateUser(User updatedUser, UUID id) {
        data.stream()
                .filter(existing -> existing.getId().equals(id))
                .findFirst()
                .map(existing -> {
                    existing.updateUserName(updatedUser.getUserName());
                    existing.updateEmail(updatedUser.getEmail());
                    existing.updatePassword(updatedUser.getPassword());
                    existing.updatePhoneNumber(updatedUser.getPhoneNumber());
                    return existing;
                });
    }

    @Override
    public void deleteUser(UUID id) {
        data.stream()
                .filter(existing -> existing.getId().equals(id))
                .findFirst()
                .map(existing -> {
                    data.remove(existing);
                    return existing;
                });
    }

    @Override
    public void deleteAll() {
        data.clear();
    }
}
