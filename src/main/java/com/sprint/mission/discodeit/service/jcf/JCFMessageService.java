package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final MessageRepository messageRepository;

    public JCFMessageService() {
        messageRepository = new JCFMessageRepository();
    }

    @Override
    public Message createMessage(User user, Channel channel, String message) {
        Message m = new Message(UUID.randomUUID(), user, channel, message, Instant.now().getEpochSecond());
        messageRepository.save(m);

        return m;
    }

    @Override
    public List<Message> getByUser(User user) {
        return messageRepository.findByUser(user);
    }

    @Override
    public List<Message> getByMessage(String message) {
        return messageRepository.findByMessage(message);
    }

    @Override
    public boolean updateById(UUID id, User user, Channel channel, String originalMessage, String updateMessage) {
        List<Message> messageList = messageRepository.findAll();
        for (Message m : messageList) {
            if (!m.getId().equals(id)) continue;
            if (!m.getUser().equals(user)) continue;
            if (!m.getChannel().equals(channel)) continue;
            m.updateMessage(updateMessage);
            break;
        }

        return true;
    }

    @Override
    public boolean removeById(UUID uuid, User user, Channel channel) {
        List<Message> messageList = messageRepository.findAll();
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
