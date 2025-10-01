package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor // Lombok: final 필드들을 매개변수로 받는 생성자를 자동 생성
@Service // 스프링 서비스 컴포넌트로 등록
public class BasicMessageService implements MessageService { // 메시지 도메인 비즈니스 로직을 구현한 서비스

    private final MessageRepository messageRepository; // 메시지 엔티티 저장/조회용 리포지토리
    //
    private final ChannelRepository channelRepository; // 채널 존재/조회 검증을 위한 리포지토리
    private final UserRepository userRepository; // 작성자(유저) 존재/조회 검증을 위한 리포지토리
    private final MessageMapper messageMapper; // Message → MessageDto 변환 매퍼
    private final BinaryContentStorage binaryContentStorage; // 첨부파일 바이트 저장/조회 스토리지
    private final BinaryContentRepository binaryContentRepository; // 첨부파일 메타데이터 저장 리포지토리
    private final PageResponseMapper pageResponseMapper; // Slice/Page → PageResponse 변환 매퍼

    @Transactional // 작성 과정 전체를 하나의 트랜잭션으로 처리
    @Override // 인터페이스 메서드 구현
    public MessageDto create(MessageCreateRequest messageCreateRequest, // 메시지 생성 요청 DTO
                             List<BinaryContentCreateRequest> binaryContentCreateRequests) { // 첨부파일 생성 요청 DTO 리스트
        UUID channelId = messageCreateRequest.channelId(); // 요청에서 채널 ID 추출
        UUID authorId = messageCreateRequest.authorId(); // 요청에서 작성자 ID 추출

        Channel channel = channelRepository.findById(channelId) // 채널 존재 여부 확인 및 조회
                .orElseThrow( // 없으면 예외 던짐
                        () -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));
        User author = userRepository.findById(authorId) // 작성자 존재 여부 확인 및 조회
                .orElseThrow( // 없으면 예외 던짐
                        () -> new NoSuchElementException("Author with id " + authorId + " does not exist")
                );

        List<BinaryContent> attachments = binaryContentCreateRequests.stream() // 첨부파일 요청 리스트 순회
                .map(attachmentRequest -> { // 각 요청을 BinaryContent 엔티티로 변환
                    String fileName = attachmentRequest.fileName(); // 파일명 추출
                    String contentType = attachmentRequest.contentType(); // 콘텐츠 타입 추출
                    byte[] bytes = attachmentRequest.bytes(); // 파일 바이트 추출

                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, // 메타데이터 엔티티 생성(파일명/크기/타입)
                            contentType);
                    binaryContentRepository.save(binaryContent); // 메타데이터 DB 저장
                    binaryContentStorage.put(binaryContent.getId(), bytes); // 실제 바이트 스토리지 저장(키: 엔티티 ID)
                    return binaryContent; // 매핑 결과 반환
                })
                .toList(); // 리스트로 수집

        String content = messageCreateRequest.content(); // 메시지 본문 추출
        Message message = new Message( // 메시지 엔티티 생성
                content, // 본문 내용
                channel, // 소속 채널
                author, // 작성자
                attachments // 첨부파일 엔티티 리스트
        );

        messageRepository.save(message); // 메시지 엔티티 저장
        return messageMapper.toDto(message); // 저장된 엔티티를 DTO로 변환해 반환
    }

    @Transactional(readOnly = true) // 조회 전용 트랜잭션으로 최적화
    @Override // 인터페이스 메서드 구현
    public MessageDto find(UUID messageId) { // 메시지 단건 조회
        return messageRepository.findById(messageId) // ID로 조회
                .map(messageMapper::toDto) // 있으면 DTO로 변환
                .orElseThrow( // 없으면 예외
                        () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Transactional(readOnly = true) // 조회 전용 트랜잭션
    @Override // 인터페이스 메서드 구현
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createAt, // 채널별 메시지 목록 조회(커서 기반)
                                                       Pageable pageable) { // 페이지 파라미터(정렬/크기)
        Slice<MessageDto> slice = messageRepository.findAllByChannelIdWithAuthor(channelId, // 채널 ID로 조회
                        Optional.ofNullable(createAt).orElse(Instant.now()), // 커서가 없으면 현재 시각을 상한으로 사용
                        pageable) // 페이징/정렬 조건 적용
                .map(messageMapper::toDto); // 엔티티 Slice를 DTO Slice로 매핑

        Instant nextCursor = null; // 다음 페이지 요청용 커서 초기값
        if (!slice.getContent().isEmpty()) { // 현재 페이지에 데이터가 있다면
            nextCursor = slice.getContent().get(slice.getContent().size() - 1) // 마지막 요소의
                    .createdAt(); // createdAt을 다음 커서로 사용
        }

        return pageResponseMapper.fromSlice(slice, nextCursor); // Slice와 커서를 PageResponse로 변환해 반환
    }

    @Transactional // 수정 작업 트랜잭션
    @Override // 인터페이스 메서드 구현
    public MessageDto update(UUID messageId, MessageUpdateRequest request) { // 메시지 내용 수정
        String newContent = request.newContent(); // 새 내용 추출
        Message message = messageRepository.findById(messageId) // 대상 메시지 조회
                .orElseThrow( // 없으면 예외
                        () -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(newContent); // 엔티티 상태 변경(더티 체킹으로 flush)
        return messageMapper.toDto(message); // 수정된 엔티티를 DTO로 반환
    }

    @Transactional // 삭제 작업 트랜잭션
    @Override // 인터페이스 메서드 구현
    public void delete(UUID messageId) { // 메시지 삭제
        if (!messageRepository.existsById(messageId)) { // 존재 여부 선검증
            throw new NoSuchElementException("Message with id " + messageId + " not found"); // 없으면 예외
        }

        messageRepository.deleteById(messageId); // 메시지 삭제 실행
    }
}

