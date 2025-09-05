package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public Message create(MessageCreateRequest req, List<BinaryContentCreateRequest> attachmentReqs) {
    var channel = channelRepository.findById(req.channelId())
        .orElseThrow(() -> new NoSuchElementException("Channel with id " + req.channelId() + " does not exist"));
    var author = userRepository.findById(req.authorId())
        .orElseThrow(() -> new NoSuchElementException("Author with id " + req.authorId() + " does not exist"));

    var message = new Message(req.content(), channel, author);

    for (var ar : attachmentReqs) {
      // 1) 메타 생성
      var meta = new BinaryContent(ar.fileName(), (long) ar.bytes().length, ar.contentType());

      // 2) 스토리지 저장 (체크드 예외 처리)
      try {
        binaryContentStorage.put(meta.getId(), ar.bytes());
      } catch (IOException e) {
        // 런타임 예외로 변환하여 트랜잭션 롤백 유도
        throw new UncheckedIOException("Failed to store binary content: " + meta.getId(), e);
      }

      // 3) 메타 저장
      var savedMeta = binaryContentRepository.save(meta);

      // 4) 메시지에 연결
      message.addAttachment(savedMeta);
    }

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
  public Slice<Message> findAllByChannelId(UUID channelId, int page, int size) {
    // 요구사항: 50개씩, 최신순
    int pageSize = 50; // 외부 size 무시하고 고정하려면 이 값만 사용
    var pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    return messageRepository.findAllByChannel_Id(channelId, pageable);
  }

  @Override
  public Message update(UUID messageId, MessageUpdateRequest request) {
    var message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    message.update(request.newContent());
    return messageRepository.save(message);
  }

  @Override
  public void delete(UUID messageId) {
    var message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

    // 메타만 삭제 (스토리지 파일 삭제는 BinaryContentStorage에 delete(UUID) 추가 후 함께 처리 권장)
    message.getAttachments().stream()
        .map(BinaryContent::getId)
        .forEach(binaryContentRepository::deleteById);

    messageRepository.deleteById(messageId);

    // TODO: BinaryContentStorage에 delete(UUID) 추가했다면 여기서 파일도 함께 삭제하세요.
  }
}
