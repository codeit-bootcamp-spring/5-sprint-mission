package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID channelId = messageCreateRequest.channelId(); // 메시지가 등록될 채널 ID 추출
        UUID authorId = messageCreateRequest.authorId(); // 작성자(유저) ID 추출

        // 존재성만 검사 (View/다른 Service 의존 X)
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " does not exist");
        }
        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("Channel with id " + authorId + " does not exist");
        }

        List<UUID> attachmentIds = binaryContentCreateRequests.stream()
                .map(attachmentRequest-> {
                    String fileName = attachmentRequest.fileName(); // 파일명
                    String contentType = attachmentRequest.contentType(); // MIME 타입
                    byte[] bytes = attachmentRequest.bytes(); // 파일 데이터

                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes); // 첨부 엔티티 생성
                    BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent); // 저장
                    return createdBinaryContent.getId(); // 저장된 첨부의 ID만 모음
                })
                .toList(); // 그걸 attachmentIds로 받음

        String content = messageCreateRequest.content(); // 본문 내용
        Message message = new Message( // 메시지 엔티티 생성
                content,
                channelId,
                authorId,
                attachmentIds
        );
        return messageRepository.save(message); // 최종 저장 후 반환
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId) // 메세지 아이디에 해당하는 Message 엔티티를 가져온다
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream() // channelId(채널 식별자)에 해당하는 모든 메시지를 찾아서 리스트로 반환합니다.
                .toList();
    }

    @Override
    public Message update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent(); // MessageUpdateRequest DTO에서 수정할 새로운 메시지 내용을 꺼냅니다.
        Message message = messageRepository.findById(messageId) // messageRepository를 이용해 해당 ID의 메시지를 찾습니다.
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(newContent); // Message 엔티티의 update 메서드를 호출해 새로운 내용으로 변경합니다.
        return messageRepository.save(message); // 수정된 엔티티를 저장합니다.
    }


    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        message.getAttachmentIds() // 메시지에 연결된 첨부파일 ID 리스트를 가져옵니다.
                .forEach(binaryContentRepository::deleteById); // 각 첨부파일을 binaryContentRepository를 통해 개별 삭제합니다.

        messageRepository.deleteById(messageId); // 첨부파일 삭제를 먼저 하고, 메시지를 삭제하는 순서로 진행하여 참조 무결성을 유지합니다.
    }

}
