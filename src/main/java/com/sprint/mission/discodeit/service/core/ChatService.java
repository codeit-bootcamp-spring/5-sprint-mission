package com.sprint.mission.discodeit.service.core;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ChatService {

    private final MessageService messageService;
    private final ChannelService channelService;

    public ChatService(MessageService messageService, ChannelService channelService) {
        this.messageService = messageService;
        this.channelService = channelService;
    }

    public Message sendMessage(UUID channelId, UUID userId, String text) {
        Channel channel = channelService.get(channelId);

        if (channel == null || !channel.getUserIds().contains(userId)) {
            throw new IllegalStateException("잘못된 접근");
        }

        Message message = new Message(text, channelId, userId);
        messageService.create(message);
        channel.addMessage(message.getId());

        return message;
    }

    public List<Message> getMessagesInChannel(UUID channelId) {
        Channel channel = channelService.get(channelId);

        if (channel == null) {
            return List.of();
        }

        return channel.getMessageIds().stream()
            .map(messageService::get)
            .filter(Objects::nonNull)
            .toList();
    }

    public List<Message> getMessagesByUser(UUID userId) {
        return messageService.getAll().stream()
            .filter(m -> m.getAuthorId().equals(userId))
            .toList();
    }
}