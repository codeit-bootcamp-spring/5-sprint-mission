package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    @Override
    public void createUser(User user) {

    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public User searchByIndex(int i) {
        return null;
    }

    @Override
    public User searchById(UUID id) {
        return null;
    }

    @Override
    public List<User> searchByName(String name) {
        return List.of();
    }

    @Override
    public List<User> getAllUsers() {
        return List.of();
    }
}
