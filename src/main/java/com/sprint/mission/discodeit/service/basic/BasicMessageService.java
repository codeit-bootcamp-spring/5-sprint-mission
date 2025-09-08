package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository; // 메시지 CRUD 담당 레포지토리
    private final ChannelRepository channelRepository; // 채널 존재/조회용 레포지토리
    private final UserRepository userRepository; // 작성자(유저) 존재/조회용 레포지토리
    private final BinaryContentRepository binaryContentRepository; // 첨부 바이너리 저장 레포지토리

    @Override
    @Transactional // 생성 전체를 하나의 트랜잭션으로 처리
    public Message create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests) { // 메시지 생성 메서드
        UUID channelId = messageCreateRequest.channelId(); // 요청에서 채널 ID 추출
        UUID authorId = messageCreateRequest.authorId(); // 요청에서 작성자 ID 추출

        Channel channel = channelRepository.findById(channelId) // 채널 엔티티 로딩
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " does not exist")); // 없으면 예외

        User author = userRepository.findById(authorId) // 작성자 엔티티 로딩
                .orElseThrow(() -> new NoSuchElementException("User with id " + authorId + " does not exist")); // 없으면 예외

        String content = messageCreateRequest.content(); // 메시지 본문 추출
        Message message = new Message(content, channel, author); // 채널/작성자 연관을 가진 메시지 엔티티 생성

        if (binaryContentCreateRequests != null) { // 첨부 요청 목록이 존재한다면
            for (BinaryContentCreateRequest req : binaryContentCreateRequests) { // 각 첨부 요청에 대해 반복
                byte[] bytes = req.bytes(); // 파일 데이터 추출
                BinaryContent file = new BinaryContent( // BinaryContent 엔티티 생성
                        req.fileName(), (long) bytes.length, req.contentType()//, bytes
                );
                BinaryContent saved = binaryContentRepository.save(file); // 첨부를 별도 저장
                message.addAttachment(saved); // 메시지에 첨부 연결(N:N, 조인테이블)
            }
        }

        return messageRepository.save(message); // 메시지 저장 후 반환(첨부 조인테이블도 함께 반영)
    }

    @Override
    @Transactional(readOnly = true) // 조회 전용 트랜잭션
    public Message find(UUID messageId) { // 메시지 단건 조회
        return messageRepository.findById(messageId) // PK로 조회
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found")); // 없으면 예외
    }

    @Override
    @Transactional(readOnly = true) // 조회 전용 트랜잭션
    public List<Message> findAllByChannelId(UUID channelId) { // 채널 기준 메시지 목록 조회
        return messageRepository.findAllByChannelId(channelId); // 레포지토리 메서드 결과 그대로 반환
    }

    @Override
    @Transactional // 수정 트랜잭션
    public Message update(UUID messageId, MessageUpdateRequest request) { // 메시지 내용 수정
        String newContent = request.newContent(); // 새 본문 추출
        Message message = messageRepository.findById(messageId) // 대상 메시지 로딩
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found")); // 없으면 예외

        message.update(newContent); // 엔티티 보조 메서드로 본문 변경(전제: Message에 update(String) 존재)
        return messageRepository.save(message); // 저장 후 변경된 엔티티 반환
    }

    @Override
    @Transactional // 삭제 트랜잭션
    public void delete(UUID messageId) { // 메시지 삭제
        Message message = messageRepository.findById(messageId) // 삭제 대상 로딩
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found")); // 없으면 예외

        // ⚠️ 첨부(BinaryContent)는 N:N 공유 가능 엔티티이며 orphanRemoval이 아님 → 메시지 삭제 시 첨부 자체는 삭제하지 않음
        messageRepository.delete(message); // 메시지 레코드 및 조인테이블 레코드만 삭제
    }


    @Override
    @Transactional(readOnly = true)
    public Slice<Message> findSliceByChannelId(UUID channelId, Pageable pageable) {
        return messageRepository.findByChannelIdOrderByCreatedAtDesc(channelId, pageable);
    }
}
