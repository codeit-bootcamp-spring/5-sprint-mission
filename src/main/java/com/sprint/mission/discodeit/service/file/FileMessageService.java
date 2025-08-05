package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.MessageDto;
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
    public MessageDto.DetailResponse create(MessageDto.CreateRequest request) {
        return null;
    }

    @Override
    public MessageDto.DetailResponse update(MessageDto.UpdateRequest request) {
        return null;
    }

    @Override
    public MessageDto.DetailResponse findById(UUID id) {
        return null;
    }

    @Override
    public List<MessageDto.DetailResponse> findAllByChannelId(UUID channelId) {
        return List.of();
    }

    @Override
    public void delete(UUID id) {
        Message message = messageRepository.findById(id).orElse(null);

        if (message != null) {
            messageRepository.delete(id);
        }
    }

    @Override
    public void deleteAll() {
        messageRepository.deleteAll();
    }
}
