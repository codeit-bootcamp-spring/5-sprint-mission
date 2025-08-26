package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    public UserService userService;
    public ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message register(Message message) {
        if (isInvalid(message.getContent()))
            throw new IllegalArgumentException("메시지 등록에 실패했습니다.");

        User user = userService.findById(message.getUserId());
        Channel channel = channelService.findById(message.getChannelId());

        data.put(message.getId(), message);
        System.out.println("메시지 : " + message.getContent() + " 등록 성공.");
        return message;
    }

    @Override
    public Message findById(UUID id) {
        if (!data.containsKey(id))
            throw new NoSuchElementException("메시지에서 해당 " + id + "를 찾을 수 없습니다.");
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID id, String newContent) {
        if (isInvalid(newContent))
            throw new IllegalArgumentException("새로운 메시지 내용을 입력하세요.");
        Message message = findById(id);

        message.setContent(newContent);
        message.setUpdatedAt(System.currentTimeMillis());
        return message;
    }

    @Override
    public Message delete(UUID id) {
        if (!data.containsKey(id))
            throw new NoSuchElementException("메시지에서 해당 " + id + "를 찾을 수 없습니다.");
        return data.remove(id);
    }

    public boolean isInvalid(String value) {
        return value == null || value.isBlank();
    }
}
