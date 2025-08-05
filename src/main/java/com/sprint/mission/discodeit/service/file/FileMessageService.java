package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

@Deprecated
public class FileMessageService implements MessageService {

    private final MessageRepository messageRepository;

    public FileMessageService(
            MessageRepository messageRepository
    ) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message addMessage(String messageContent, UUID userId) {
        Message message = new Message(messageContent, userId);
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
        messageRepository.delete(messageId);
    }

    @Override
    public void deleteAllMessage() {
        messageRepository.deleteAll();
    }
}
