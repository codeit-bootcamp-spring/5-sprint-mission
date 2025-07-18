package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User createUser(String username, String password, String nickname);

    Optional<User> findUser(UUID userId);

    List<User> findAllUsers();

    User updateUser(UUID userId, String username, String password, String nickname);

    User deleteUser(UUID userId);

}
