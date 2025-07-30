package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messages = new HashMap<>();

    UserService userService;
    ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(UUID senderId, UUID channelId, String name, String title, String content) {
        if (userService.readUser(senderId) == null || channelService.readChannel(channelId) == null) {
            return null;
        }
        Message message = new Message(senderId, channelId, name, title, content);
        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public Message readMessage(UUID id) {
        return messages.get(id);
    }

    @Override
    public List<Message> readAllMessages() {
        return messages.values().stream().collect(Collectors.toList());
    }

    @Override
    public Message updateName(UUID id, String name) {
        if(messages.containsKey(id)){
            messages.get(id).setName(name);
            return messages.get(id);
        }
        return null;
    }

    @Override
    public Message updateTitle(UUID id, String title) {
        if(messages.containsKey(id)){
            messages.get(id).setTitle(title);
            return messages.get(id);
        }
        return null;
    }

    @Override
    public Message updateContent(UUID id, String content) {
        if(messages.containsKey(id)){
            messages.get(id).setContent(content);
            return messages.get(id);
        }
        return null;
    }

    @Override
    public boolean deleteMessage(UUID id) {
        if(messages.containsKey(id)){
            messages.remove(id);
            return true;
        }
        return false;
    }
}
