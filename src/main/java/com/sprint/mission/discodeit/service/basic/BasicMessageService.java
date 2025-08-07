package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements com.sprint.mission.discodeit.service.MessageService {

    private final MessageRepository messageRepository;

    @Override
    public Message addMessage(String messageContent, UUID userId, UUID channelId) {
        Message message = new Message(messageContent, userId, channelId);
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
