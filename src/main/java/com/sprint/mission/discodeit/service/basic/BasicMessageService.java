package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    @Override
    public Message create(MessageCreateDto dto) {
        Message message = new Message(dto.userId(), dto.channelId(), dto.content());
        // 첨부한 파일이 존재하면
        if (dto.files() != null && !dto.files().isEmpty()) {
            for (BinaryContent file : dto.files()) {
                message.addFile(file.getId());
            }
        }
        messageRepository.save(message);
        return message;
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));
    }

    @Override
    public List<Message> findByUserId(UUID userId) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findByContent(String content) {
        List<Message> result = new ArrayList<>();
        for (Message message : messageRepository.findAll()) {
            if (message.getContent().contains(content)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public Message update(UUID id, String newMessage) {
        Message message = messageRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        message.updateContent(newMessage);
        messageRepository.save(message);
        return message;
    }

    @Override
    public void attachFile(UUID messageId, UUID fileId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        message.addFile(fileId); // 도메인 객체 내 로직 호출
        messageRepository.save(message); // 변경사항 저장
    }

    @Override
    public void detachFile(UUID messageId, UUID fileId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        message.removeFile(fileId);
        messageRepository.save(message);
    }

    @Override
    public boolean delete(UUID id) {
        return messageRepository.deleteById(id);
    }
}
