package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    public boolean addUser(User user);
    public List<User> getUsers();
    public User getUserById(UUID id);
    public User getUserByUsername(String username);
    public User updateUser(User user, UUID id);
    public User deleteUser(UUID id);
    public void deleteAll();

}
