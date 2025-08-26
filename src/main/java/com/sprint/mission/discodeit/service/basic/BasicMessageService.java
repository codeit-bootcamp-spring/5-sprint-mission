package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.binaryContent.FileDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentService binaryContentService;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(UUID userId, UUID channelId, String content, List<FileDto> fileDtoList) {

        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        channelRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        Message message = new Message(userId, channelId, content);

        // 첨부한 파일이 존재하면
        if (fileDtoList != null && !fileDtoList.isEmpty()) {
            fileDtoList.forEach(fileDto -> {
                UUID messageFileId = binaryContentService.save(fileDto);
                message.addFile(messageFileId);
            });
        }

        messageRepository.save(message);
        return message;
    }

    @Override
    public Message update(MessageRequest.update dto) {

        Message message = messageRepository.findById(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        message.updateContent(dto.content());
        return messageRepository.save(message);
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

    public List<Message> findByChannel(UUID channelId) {
        return messageRepository.findByChannel(channelId);
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
