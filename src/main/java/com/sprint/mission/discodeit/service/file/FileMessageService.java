package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final FileMessageRepository messageRepository;

    public FileMessageService(FileMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message create(Message message) {

        if (message == null) {
            return null;
        }

        return messageRepository.save(message);
    }

    @Override
    public List<Message> getAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message get(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public Message update(UUID id, String text) {
        Message message = messageRepository.findById(id);

        if (message == null) {
            return null;
        }

        message.update(text);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        Message message = messageRepository.findById(id);

        if (message != null) {
            messageRepository.deleteById(id);
        }
    }

    @Override
    public void deleteAll() {
        messageRepository.deleteAll();
    }
}
