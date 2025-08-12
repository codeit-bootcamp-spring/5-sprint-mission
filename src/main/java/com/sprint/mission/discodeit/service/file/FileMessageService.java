package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final MessageRepository repository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public FileMessageService(MessageRepository repository,
                              UserRepository userRepository,
                              ChannelRepository channelRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message create(Message message) {
        return repository.save(message);
    }

    @Override
    public Message read(UUID id) {
        return repository.findById(id); // ← 여기가 핵심!
    }

    @Override
    public List<Message> readAll() {
        return repository.findAll();
    }

    @Override
    public boolean update(UUID id, String newContent) {
        try {
            Message old = repository.findById(id);
            if (old == null) return false;
            Message updated = old.withContent(newContent);
            repository.update(id, updated);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void delete(UUID id) {
        repository.delete(id);
    }
}

