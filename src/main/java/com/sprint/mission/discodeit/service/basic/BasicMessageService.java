package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final Map<UUID, User> userData;
    private final Map<UUID, Channel> channelData;

    private final Map<String, User> userByName;
    private Map<UUID, Message> data;

    public BasicMessageService(MessageRepository messageRepository,
                               Map<UUID, User> userData,
                               Map<UUID, Channel> channelData) {
        this.messageRepository = messageRepository;
        this.userData = userData;
        this.channelData = channelData;
        this.data = messageRepository.loadData();

        this.userByName = new HashMap<>();
        for (User user : userData.values()) {
            userByName.put(user.getUserName(), user);
        }
    }

    @Override
    public void create(Message message) {
        if (!userByName.containsKey(message.getSender())) {
            throw new IllegalArgumentException("존재하지 않는 보낸 사람입니다: " + message.getSender());
        }


        data.put(message.getId(), message);
        messageRepository.save(data);
    }

    @Override
    public Message find(UUID id) {
        return data.get(id);
    }

    @Override
    public ArrayList<Message> allFind() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, Message message) {
        if (data.containsKey(id)) {
            data.put(id, message);
            messageRepository.save(data);
        }
    }

    @Override
    public void delete(UUID id) {
        if (data.remove(id) != null) {
            messageRepository.save(data);
        }

    }
}
