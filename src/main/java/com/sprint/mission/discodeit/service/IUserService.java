package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    User createUser(String usernmae, String passowrd, String email);
    User findById(UUID userId);
    List<User> findAll(String searchStr);
    List<User> update(UUID userId, String chgColumnType, String username, String password);
    List<User> delete(UUID userId);
}
