package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.service.dto.message.MessageUpdateRequest;
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

    /**
     * Service 내부 인덱스: messageId -> attachmentIds
     * - 엔티티/리포지토리 변경 없이 ‘삭제 시 첨부도 함께 삭제’ 구현
     * - 앱 재기동 시 초기화(메모리 한계)됨을 유의
     */
    private final Map<UUID, List<UUID>> attachmentIndex = new ConcurrentHashMap<>();

    // ===== 신규 권장 API =====

    @Override
    public Message create(MessageCreateRequest req) {
        validateCreate(req);

        // 존재성만 검사 (View/다른 Service 의존 X)
        if (!channelRepository.existsById(req.getChannelId())) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다. id=" + req.getChannelId());
        }
        if (!userRepository.existsById(req.getAuthorId())) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + req.getAuthorId());
        }

        // 1) 메시지 저장 (세터 없이 생성자)
        Message saved = messageRepository.save(
                new Message(req.getContent().trim(), req.getChannelId(), req.getAuthorId())
        );

        // 2) 첨부 저장(선택) + 내부 인덱스에 연결
        if (req.getAttachments() != null && !req.getAttachments().isEmpty()) {
            List<UUID> ids = new ArrayList<>();
            for (MessageCreateRequest.AttachmentCreate a : req.getAttachments()) {
                if (a == null || a.getData() == null || a.getData().length == 0) continue;

                BinaryContent bc = BinaryContent.of(
                        a.getContentType(),         // contentType
                        a.getFilename(),            // originalName
                        a.getData().length,         // size
                        a.getFilename(),            // storageKey(스토리지 미사용이니 파일명으로 대체)
                        a.getData()                 // data
                );
                BinaryContent savedBc = binaryContentRepository.save(bc);
                ids.add(savedBc.getId());
            }
            if (!ids.isEmpty()) {
                attachmentIndex.put(saved.getId(), ids);
            }
        }
        return saved;
    }

    @Override
    public Message update(MessageUpdateRequest req) {
        Objects.requireNonNull(req, "요청은 필수입니다.");
        Objects.requireNonNull(req.getMessageId(), "messageId는 필수입니다.");

        String content = Optional.ofNullable(req.getNewContent()).map(String::trim).orElse("");
        if (content.isBlank()) throw new IllegalArgumentException("내용은 비어 있을 수 없습니다.");

        Message found = messageRepository.findById(req.getMessageId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다. id=" + req.getMessageId()));

        // 도메인 메서드로 갱신 (세터 없음)
        found.update(content);
        return messageRepository.save(found);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        List<Message> all = messageRepository.findAll();
        if (channelId == null) return all;
        return all.stream()
                .filter(m -> channelId.equals(m.getChannelId()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID messageId) {
        Objects.requireNonNull(messageId, "messageId는 필수입니다.");

        // 메시지 존재 확인
        if (!messageRepository.existsById(messageId)) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다. id=" + messageId);
        }

        // 1) 내부 인덱스 기준으로 첨부 먼저 삭제
        List<UUID> attachmentIds = attachmentIndex.getOrDefault(messageId, List.of());
        for (UUID aid : attachmentIds) {
            if (binaryContentRepository.existsById(aid)) {
                binaryContentRepository.deleteById(aid);
            }
        }
        attachmentIndex.remove(messageId);

        // 2) 메시지 삭제
        messageRepository.deleteById(messageId);
    }

    // ===== 기존 호환 API =====

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        return create(new MessageCreateRequest(content, channelId, authorId, List.of()));
    }

    @Override
    public Message find(UUID messageId) {
        Objects.requireNonNull(messageId, "messageId는 필수입니다.");
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다. id=" + messageId));
    }

    @Override
    public List<Message> findAll() {
        // 호환 목적: 전체 조회는 channelId=null 처리
        return findAllByChannelId(null);
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        return update(new MessageUpdateRequest(messageId, newContent));
    }

    // ===== 유효성 =====
    private void validateCreate(MessageCreateRequest req) {
        Objects.requireNonNull(req, "요청은 필수입니다.");
        if (req.getContent() == null || req.getContent().isBlank())
            throw new IllegalArgumentException("content는 비어 있을 수 없습니다.");
        Objects.requireNonNull(req.getChannelId(), "channelId는 필수입니다.");
        Objects.requireNonNull(req.getAuthorId(), "authorId는 필수입니다.");
    }
}
