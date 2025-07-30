package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User createUser(String nick,String email, String pass);

//    UUID addMessage(UUID userId,UUID messageId);
//
//    UUID addChannel(UUID userId,UUID channelId);

    User getUserById(UUID userId);

    UUID getUserIdByNick(String nick);

    List<User> getAllUsers();

    User updateUserNick(UUID userId, String nick);

    User updateUserPass(UUID userId, String pass);

    User updateUserEmail(UUID userId, String email);

    User deleteUser(UUID userId);
}
