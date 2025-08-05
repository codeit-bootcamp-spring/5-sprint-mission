package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    private final FileMessageRepository messageRepo;

    public FileMessageService(FileMessageRepository messageRepo) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map",
                Message.class.getSimpleName());
        this.messageRepo = messageRepo;
    }

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        Message message = new Message(content, channelId, authorId);
        return messageRepo.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepo.find(messageId);
    }

    @Override
    public List<Message> findAll() {
        return messageRepo.findAll();
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        Message message = messageRepo.find(messageId);
        message.update(newContent);
        return messageRepo.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        messageRepo.delete(messageId);
    }
}
