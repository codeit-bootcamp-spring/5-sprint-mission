package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(String channelName, String nickName, String message);
    Message find(UUID uuid);
    List<Message> findAll();
    Message updateMessage(UUID uuid, String Message);
    Message deleteMessage(UUID uuid);
}


