package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.common.FileUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileMessageRepository implements MessageRepository {
    private final Path MSG_DIR = Path.of(Message.class.getSimpleName());

    public FileMessageRepository() {
        FileUtils.init(MSG_DIR);
    }

    @Override
    public Message save(Message message) {
        Path path = MSG_DIR.resolve(message.getId().toString());
        FileUtils.save(path, message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Path path = MSG_DIR.resolve(id.toString());
        return Optional.ofNullable(FileUtils.findOne(path, Message.class));
    }

    @Override
    public Optional<Message> findLatestByChannelId(UUID channelId) {
        return this.findAllByChannelId(channelId).stream()
                .max(Comparator.comparing(Message::getCreatedAt));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return FileUtils.findAll(MSG_DIR, Message.class).stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        Path path = MSG_DIR.resolve(id.toString());
        FileUtils.delete(path);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        this.findAllByChannelId(channelId).stream()
                .map(Message::getId)
                .forEach(this::delete);
    }

    public List<Message> findAll() {
        return FileUtils.findAll(MSG_DIR, Message.class);
    }

    public void deleteAll() {
        FileUtils.deleteAll(MSG_DIR);
    }
}
