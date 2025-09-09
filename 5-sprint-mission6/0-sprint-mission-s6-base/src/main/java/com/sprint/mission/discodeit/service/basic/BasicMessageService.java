package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentService binaryContentService;

  @Override
  @Transactional
  public Message create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {

    // 1) 필수 엔티티 로딩 (참조 기반)
    UUID channelId = messageCreateRequest.channelId();
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));

    UUID authorId = messageCreateRequest.authorId();
    User author = null;
    if (authorId != null) { // author는 null 허용
      author = userRepository.findById(authorId)
          .orElseThrow(() -> new NoSuchElementException("Author with id " + authorId + " does not exist"));
    }

    // 2) 첨부 저장 (메타는 DB, bytes는 Storage) → BinaryContent 엔티티 목록 획득
    List<BinaryContent> attachments = (binaryContentCreateRequests == null)
        ? List.of()
        : binaryContentCreateRequests.stream()
            .filter(Objects::nonNull)
            .map(binaryContentService::create) // BinaryContent 반환
            .toList();

    // 3) 메시지 생성/저장 (참조 기반 생성자)
    String content = messageCreateRequest.content();
    Message message = new Message(content, channel, author, attachments);
    return messageRepository.save(message);
  }

  @Override
  @Transactional(readOnly = true)
  public Message find(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Message> findAllByChannelId(UUID channelId) {
    // ID 기반이 아니라 참조 경로 기반 메서드 사용
    return messageRepository.findByChannel_Id(channelId);
  }

  @Override
  @Transactional
  public Message update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    if (message.update(request.newContent())) {
      // 더티체킹으로 플러시되지만, 명시 save도 무방
      return messageRepository.save(message);
    }
    return message;
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    // 첨부 BinaryContent는 재사용 가능성이 있어 삭제하지 않음.
    // 조인테이블의 매핑 행은 메시지 삭제 시 자동 정리됨.
    if (!messageRepository.existsById(messageId)) {
      throw new NoSuchElementException("Message with id " + messageId + " not found");
    }
    messageRepository.deleteById(messageId);
  }
}