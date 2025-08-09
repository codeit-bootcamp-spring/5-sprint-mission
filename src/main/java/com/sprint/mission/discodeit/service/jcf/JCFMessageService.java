package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.main.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;

    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();

        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(UUID authorId, UUID channelId, String content) {
        try {
            userService.findById(authorId);
            channelService.findById(channelId);
        } catch (NoSuchElementException e) {
            throw e;
        }

        Message message = new Message(
                authorId,
                channelId,
                content
        );
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
    }

    @Override
    public List<Message> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
        message.update(content);
        return message;
    }

    @Override
    public void delete(UUID id) {
        Message message = findById(id);
        data.remove(message.getId());
    }
}