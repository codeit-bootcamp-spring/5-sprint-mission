package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {

    private final String DIRECTORY;
    private final String EXTENSION;

    public FileMessageRepository() {
        this.DIRECTORY = "USER";
        this.EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if (!path.toFile().exists()) {
            try {
                Files.createDirectory(path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public Message save(Message message) {
        return null;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        return List.of();
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return List.of();
    }

    @Override
    public void update(UUID id, String newContent) {

    }

    @Override
    public void delete(UUID id) {

    }
}
