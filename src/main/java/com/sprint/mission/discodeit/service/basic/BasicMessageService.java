package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service("basicMessageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponse create(MessageCreateRequest request) {
        // 1. 채널, 유저 존재 여부 확인
        if (!channelRepository.existsById(request.getChannelId())) {
            throw new RuntimeException("존재하지 않는 채널입니다.");
        }

        if (!userRepository.existsById(request.getUserId())) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        // 2. 첨부파일 ID 리스트 유효성 확인
        List<UUID> attachmentIds = request.getAttachmentIds();
        if (attachmentIds != null) {
            for (UUID id : attachmentIds) {
                if (!binaryContentRepository.existsById(id)) {
                    throw new RuntimeException("존재하지 않는 첨부파일입니다: " + id);
                }
            }
        }

        // 3. 메시지 생성 및 저장
        Message message = new Message(
                request.getUserId(),
                request.getChannelId(),
                request.getContent(),
                attachmentIds
        );
        messageRepository.save(message);

        // 4. 결과 반환
        return new MessageResponse(
                message.getId(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getCreatedAt(),
                message.getAttachmentIds()
        );
    }


    @Override
    public List<MessageResponse> findByChannelId(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new RuntimeException("존재하지 않는 채널입니다.");
        }
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getAuthorId(),
                        message.getChannelId(),
                        message.getCreatedAt(),
                        message.getAttachmentIds()
                )).toList();
    }

    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다."));

        message.update(request.getContent());

        messageRepository.save(message);

        return new MessageResponse(
                message.getId(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getCreatedAt(),
                message.getAttachmentIds()
        );
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다."));

        List<UUID> attachmentIds = message.getAttachmentIds();
        if (attachmentIds != null) {
            for (UUID id : attachmentIds) {
                binaryContentRepository.deleteById(id);
            }
        }

        messageRepository.deleteById(messageId);
    }
}
