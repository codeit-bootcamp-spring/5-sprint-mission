package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageService implements MessageService{

    private final FileMessageRepository messageRepo;

    public FileMessageService(FileMessageRepository messageRepo) {
        this.messageRepo = messageRepo;
    }

    @Override
    public Message createMessage(String content) {
        return messageRepo.save(new Message(content));
    }

    @Override
    public Optional<Message> getMessage(UUID messageId) {
        return messageRepo.findById(messageId);
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepo.findAll();
    }

    @Override
    public Message updateMessage(UUID messageId, String content) {
        return messageRepo.update(messageId, content);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        messageRepo.delete(messageId);
    }

    @Override
    public boolean existsById(UUID id) {
        return messageRepo.existById(id);
    }
}
