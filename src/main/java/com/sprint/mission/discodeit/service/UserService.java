package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AddUserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User addUser(AddUserDto addUserDto);
    User getUserById(UUID userId);
    List<User> getAllUser();
    User updateUser(UUID userId, AddUserDto addUserDto);
    void deleteUser(UUID userId);
    void deleteAllUser();

    void joinChannel(UUID channelId, User user);
    void exitChannel(UUID channelId, User user);


}
