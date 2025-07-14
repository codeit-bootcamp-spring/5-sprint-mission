package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private final List<Message> messageList;

    public JCFMessageService() {
        messageList = new ArrayList<>();
    }

    @Override
    public Message createMessage(User user, Channel channel, String message) {
        Message m = new Message(UUID.randomUUID(), user, channel, message, Instant.now().getEpochSecond());
        messageList.add(m);

        return m;
    }

    @Override
    public List<Message> findByUser(User user) {
        return messageList.stream()
                .filter(message -> message.getUser().equals(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> searchByMessage(String message) {
        return messageList.stream()
                .filter(m -> m.getMessage().contains(message))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateMessage(UUID id, User user, Channel channel, String message) {
        for (Message m : messageList)
        {
            if (!m.getId().equals(id)) return false;
            if (!m.getUser().equals(user)) return false;
            if (!m.getChannel().equals(channel)) return false;

            m.setMessage(message);
            m.setUpdateAt(Instant.now().getEpochSecond());
            break;
        }

        return true;
    }

    @Override
    public boolean deleteMessage(UUID uuid, User user, Channel channel) {
        for (Message m : messageList)
        {
            if (!m.getId().equals(uuid)) return false;
            if (!m.getUser().equals(user)) return false;
            if (!m.getChannel().equals(channel)) return false;

            messageList.remove(m);
            break;
        }

        return true;
    }
}
