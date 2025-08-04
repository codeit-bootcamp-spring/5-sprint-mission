package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(User user);

    User updateName(UUID id, String name);

    User addChannel(UUID id, Channel channel);

    User deleteChannel(UUID id, Channel channel);

    User delete(UUID id);

    void deleteAll();

    User searchById(UUID id);

    List<User> searchByName(String name);

    List<User> searchAll();
}
