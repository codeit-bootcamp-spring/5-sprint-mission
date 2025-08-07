package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.AddMessageDto;
import com.sprint.mission.discodeit.dto.request.UpdateMessageDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message getMessageById(UUID messageId);

    List<Message> getAllMessage();

    Message updateMessage(UpdateMessageDto updateMessageDto);

    void deleteMessage(UUID messageId);

    void deleteAllMessage();

    Message addMessage(AddMessageDto addMessageDto);

    List<Message> getAllByChannelId(UUID channelId);
}