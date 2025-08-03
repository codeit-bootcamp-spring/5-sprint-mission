package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;

    private final ChannelService channelService;
    private final UserService userService;

    public JCFMessageService(ChannelService channelService, UserService userService) {
        data = new HashMap<>();
        this.channelService = channelService;
        this.userService = userService;
    }

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        if (content == null || content.isBlank() || channelId == null || authorId == null) {
            throw new IllegalArgumentException("message content or channelId or authorId is null or blank");
        }

        try {
            channelService.find(channelId);
            userService.find(authorId);
        } catch (NoSuchElementException e) {
            throw e;
        }

        Message message = new Message(content, channelId, authorId);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message find(UUID messageId) {
        if (!data.containsKey(messageId)) {
            throw new NoSuchElementException("message not found");
        }
        return data.get(messageId);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        Message message = data.get(messageId);

        if (message == null) {
            throw new NoSuchElementException("message not found");
        }

        message.update(newContent);
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        if (!data.containsKey(messageId)) {
            throw new NoSuchElementException("message not found");
        }
        data.remove(messageId);
    }
}
