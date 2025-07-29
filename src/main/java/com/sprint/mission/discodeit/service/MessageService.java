package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message createMessage(String content,UUID channelId, UUID authorId);

    Message getMessageById(UUID messageId);

    List<Message> getAllMessages();

    List<Message> getAllMessagesByChannelId(UUID channelId);

    List<Message> getAllMessagesByAuthorId(UUID authorId);

//    List<Message> getMessageByNick(String nick);

    Message updateMessageContent(UUID messageId, String content);

    Message deleteMessage(UUID messageId);

}
