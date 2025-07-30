package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    public void createUser(String username,String password );
    public User readByIdUser(UUID name);
    public void readAllUser();
    public void updateUser(UUID user, String username,String password );
    public void deleteByIdUser(UUID user);
}
