package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageDTO;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(Channel channel, String message, User author, boolean tts);
    Message findById(UUID messageId);
    List<Message> findAll();
    Message update(MessageDTO messageDTO);
    MessageDTO createMessageDTO(UUID messageId, UUID channelId, String message, User author, boolean allMentioned);
    Message deleteById(UUID messageId);
}
