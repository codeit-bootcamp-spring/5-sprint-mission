package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {

    private static final String directoryName = "messages";

    @Override
    public Optional<Message> save(Message message) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        return List.of();
    }

    @Override
    public void delete(Message message) {

    }

    @Override
    public void deleteAll() {

    }
}
