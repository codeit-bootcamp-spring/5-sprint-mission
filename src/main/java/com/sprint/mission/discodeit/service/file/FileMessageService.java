package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final MessageRepository messageRepository;

    public FileMessageService() {
        messageRepository = new FileMessageRepository();
    }

    @Override
    public Message createMessage(User user, Channel channel, String message) {
        Message m =  new Message(user, channel, message);
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
        return messageRepository.findAll().stream()
                .filter(m -> m.getId().equals(id))
                .filter(m -> m.getUser().equals(user))
                .filter(m -> m.getChannel().equals(channel))
                .filter(m -> m.getMessage().equals(originalMessage))
                .findFirst()
                .map(m -> {
                    Message update = new Message(id, user, channel, updateMessage, m.getCreateAt());
                    messageRepository.update(id, update);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean removeById(UUID id, User user, Channel channel) {
        return messageRepository.findAll()
                .removeIf(m -> m.getId().equals(id) && m.getUser().equals(user) && m.getChannel().equals(channel));
    }
}
