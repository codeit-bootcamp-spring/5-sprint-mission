package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageFindResponse;
import com.sprint.mission.discodeit.dto.response.MessageUpdateResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("messageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest request) {
        if (!channelRepository.findById(request.channelId()).isPresent()) {
            throw new NoSuchElementException("Channel not found with id " + request.channelId());
        }
        if (!userRepository.findById(request.userId()).isPresent()) {
            throw new NoSuchElementException("Author not found with id " + request.userId());
        }

        Message message = new Message(request.content(),request.channelId(),request.userId(),
                request.attachmentIds() != null ? request.attachmentIds() : new ArrayList<>()
                );
        return messageRepository.save(message);
    }

    @Override
    public Message findByUserId(UUID userId) {
        return messageRepository.findByUserId(userId).orElseThrow(
                () -> new NoSuchElementException("User not found with id " + userId));
    }


    // 특정 Channel의 Message 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다.
    // findallByChannelId
    @Override
    public List<MessageFindResponse> findAllByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(MessageFindResponse::new)
                .toList();
    }


    @Override
    public List<MessageFindResponse> findAll() {
        return messageRepository.findAll().stream()
                .map(MessageFindResponse::new) // 엔터테 -> DTO
                .toList();
    }

    @Override
    public MessageUpdateResponse update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + request.messageId() + " not found"));
        message.update(request.content());
        Message newMessage = messageRepository.save(message);
        return new MessageUpdateResponse(newMessage);
    }

    // 관련된 도메인도 같이 삭제합니다.
    // 첨부파일(BinaryContent)
    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }
        messageRepository.deleteById(messageId);
        Message message = messageRepository.findById(messageId).orElseThrow(
                () -> new NoSuchElementException("Message with id " + messageId + " not found")
        );
        binaryContentRepository.deleteByAttachmentId(message.getAttachmentIds());
    }
}
