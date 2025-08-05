package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final FileMessageRepository messageRepository = new FileMessageRepository();

    @Override
    public Message create(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID id, String newContent) {
        Message existing = messageRepository.findById(id);
        existing.updateTimestamp(); // 필드 수정은 명시되지 않았으므로 제외
        return messageRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        messageRepository.deleteById(id);
    }
}
