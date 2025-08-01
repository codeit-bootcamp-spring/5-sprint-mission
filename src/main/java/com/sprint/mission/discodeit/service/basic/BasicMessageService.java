package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    @Override
    public Message create(Message message) {

        if (message == null) {
            return null;
        }

        if (message.getText() == null || message.getChannelId() == null || message.getAuthorId() == null) {
            return null;
        }

        return messageRepository.save(message);
    }

    @Override
    public Message create(String text, UUID channelId, UUID userId) {

        if (text == null || channelId == null || userId == null) {
            return null;
        }

        return messageRepository.save(new Message(text, channelId, userId));
    }

    @Override
    public List<Message> getAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message get(UUID id) {
        return messageRepository.findById(id).orElse(null);
    }

    @Override
    public Message update(UUID id, String text) {
        Message message = messageRepository.findById(id).orElse(null);

        if (message == null) {
            return null;
        }

        message.update(text);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        Message message = messageRepository.findById(id).orElse(null);

        if (message != null) {
            messageRepository.deleteById(id);
        }
    }

    @Override
    public void deleteAll() {
        messageRepository.deleteAll();
    }
}
