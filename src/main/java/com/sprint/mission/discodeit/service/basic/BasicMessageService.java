package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;

    public BasicMessageService(
            MessageRepository messageRepository
    ) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message addMessage(String messageContent, Channel channel, User user) {
        Message message = new Message(messageContent, channel, user);
        return messageRepository.save(message).orElseThrow();
    }

    @Override
    public Message getMessageById(UUID messageId) {
        return messageRepository.findById(messageId).orElseThrow();
    }

    @Override
    public List<Message> getAllMessage() {
        return messageRepository.findAll();
    }

    @Override
    public Message updateMessage(UUID messageId, String messageContent) {
        Message message = messageRepository.findById(messageId).orElseThrow();
        message.updateContent(messageContent);
        return messageRepository.save(message).orElseThrow();
    }

    @Override
    public void deleteMessage(UUID messageId) {
        messageRepository.findById(messageId).ifPresent(messageRepository::delete);
    }

    @Override
    public void deleteAllMessage() {
        messageRepository.deleteAll();
    }
}
