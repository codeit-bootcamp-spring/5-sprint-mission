package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(User user, Channel channel, String message);
    List<Message> findByUser(User user);
    List<Message> searchByMessage(String message);
    boolean updateMessage(UUID id, User user, Channel channel, String message);
    boolean deleteMessage(UUID id, User user, Channel channel);
}
