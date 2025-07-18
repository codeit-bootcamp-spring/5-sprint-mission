package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private static final JCFMessageService instance = new JCFMessageService();
    private final Map<UUID, Message> messages = new HashMap<>();
    private UserService userService;
    private ChannelService channelService;


    private JCFMessageService() {
    }

    public static JCFMessageService getInstance() { return instance; }

    public void setDependencies(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }


    @Override
    public void create(Message message) {
        if (userService.findById(message.getUserId()) == null){
            System.out.println("유저가 존재하지 않아 메시지를 생성할 수 없습니다.");
            return;
        }

        if (channelService.findById(message.getChannelId()) == null){
            System.out.println("채널이 존재하지 않아 메시지를 생성할 수 없습니다.");
            return;
        }

        messages.putIfAbsent(message.getId(),message);
    }

    @Override
    public Message findById(UUID id) {
        return messages.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();
        for (Message message : messages.values()) {
            if (message.getChannelId().equals(channelId)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public void update(UUID id, String newContent) {
        Message msg = messages.get(id);
        if (newContent != null) {
            msg.updateContent(newContent);
        } else {
            System.out.println("해당 ID의 메세지가 없습니다.");
        }
    }

    @Override
    public void delete(UUID id) {
        messages.remove(id);
    }
}
