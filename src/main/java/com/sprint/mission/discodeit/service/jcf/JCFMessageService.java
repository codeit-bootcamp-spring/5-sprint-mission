package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;
    UserService userService;
    ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
        data = new HashMap<>();
    }

    @Override
    public Message create(Message message) {
        userService.searchById(message.getSenderId());
        channelService.searchById(message.getChannelId());
        return data.put(message.getId(), message);
    }

    @Override
    public Message updateContent(UUID id, String content) {
        Message message = searchById(id);
        message.updateContent(content);
        return create(message);
    }

    @Override
    public Message updateSenderId(UUID id, UUID senderId) {
        Message message = searchById(id);
        message.updateSenderId(senderId);
        return create(message);
    }

    @Override
    public Message updateChannelId(UUID id, UUID channelId) {
        Message message = searchById(id);
        message.updateChannelId(channelId);
        return create(message);
    }

    @Override
    public Message delete(UUID id) {
        Message message = searchById(id);
        return data.remove(message.getId());
    }

    @Override
    public void deleteAll() {
        data.clear();
    }

    @Override
    public Message searchById(UUID id) {
        Message message = data.getOrDefault(id, null);
        if (message == null) {
            throw new NoSuchElementException("해당하는 메세지를 찾을 수 없습니다.");
        }
        return message;
    }

    @Override
    public List<Message> searchByContent(String content) {
        List<Message> messages = new ArrayList<>();
        for (Message message : data.values()) {
            if (message.getContent().contains(content)) {
                messages.add(message);
            }
        }
        return new ArrayList<>(messages);
    }

    @Override
    public List<Message> searchBySenderId(UUID id) {
        List<Message> messages = new ArrayList<>();
        for (Message message : data.values()) {
            if (message.getSenderId().equals(id)) {
                messages.add(message);
            }
        }
        return new ArrayList<>(messages);
    }

    @Override
    public List<Message> searchAll() {
        return new ArrayList<>(data.values());
    }
}
