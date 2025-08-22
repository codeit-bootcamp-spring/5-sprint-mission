package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.common.FileUtils;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileMessageRepository implements MessageRepository {
    private final Path MSG_DIR = Path.of("message");

    public FileMessageRepository() {
        FileUtils.init(MSG_DIR);
    }

    @Override
    public Message save(Message messageDto) {
        Path path = MSG_DIR.resolve(messageDto.getId().toString());
        FileUtils.save(path, messageDto);
        return messageDto;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Path path = MSG_DIR.resolve(id.toString());
        return Optional.ofNullable(FileUtils.findOne(path, Message.class));
    }

    @Override
    public List<Message> findAll() {
        return FileUtils.findAll(MSG_DIR, Message.class);
    }

    @Override
    public void delete(UUID id) {
        Path path = MSG_DIR.resolve(id.toString());
        FileUtils.delete(path);
    }

    @Override
    public void deleteAll() {
        FileUtils.deleteAll(MSG_DIR);
    }
}
