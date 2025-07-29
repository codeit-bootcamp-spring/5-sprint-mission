package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private final MessageRepository messageRepository = new FileMessageRepository();

    @Override
    public Message createMessage(String content, UUID channelId, UUID authorId) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메세지 내용을 입력하세요.");
        }
        Message message = new Message(content, channelId, authorId);
        return messageRepository.save(message);
    }

    @Override
    public Optional<Message> getMessage(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public Message updateMessage(UUID id, String content) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 메세지가 없습니다."));
        return messageRepository.update(message.update(content));
    }

    @Override
    public Message deleteMessage(UUID id) {
        return messageRepository.delete(id);
    }
}
