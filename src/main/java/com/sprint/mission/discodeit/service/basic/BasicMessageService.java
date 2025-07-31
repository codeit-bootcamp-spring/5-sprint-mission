package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private MessageRepository messageRepository;

    public BasicMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message createMessage(User user, Channel channel, String message) {
        Message m = new Message(user, channel, message);
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
    public boolean updateById(UUID id, User user, Channel channel, String message) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getId().equals(id))
                .filter(m -> m.getUser().equals(user))
                .filter(m -> m.getMessage().equals(message))
                .findFirst()
                .map(m -> {
                    Message updateMessage = new Message(m.getId(), user, channel, message, m.getCreateAt());
                    messageRepository.update(id, updateMessage);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean removeById(UUID id, User user, Channel channel) {
        return messageRepository.delete(id);
    }
}
