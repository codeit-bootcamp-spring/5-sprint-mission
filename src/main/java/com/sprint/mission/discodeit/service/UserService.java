package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserService {
    public User createUser(String email, String username, String password, String discriminator, UserStatus status);
    public User findById(UUID userId);
    List<User> findAll();
    User update(UUID userId, String email, String username, String password, String discriminator, UserStatus status);
    User deleteById(UUID userId);
    void checkValidate(String email, String username, String password, String discriminator, UserStatus status);
}
