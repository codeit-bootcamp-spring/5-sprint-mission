package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data = new HashMap<>();
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    public Message create(String content, UUID userId, UUID channelId) {
        if (userService.findById(userId) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if (channelService.findById(channelId) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        Message message = new Message(content, userId, channelId);
        data.put(message.getId(), message);
        return message;
    }

    public Message findById(UUID id) {
        return data.get(id);
    }

    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    public void update(UUID id, String newContent) {
        Message message = data.get(id);
        if (message != null) message.updateContent(newContent);
    }

    public void delete(UUID id) {
        data.remove(id);
    }
}
