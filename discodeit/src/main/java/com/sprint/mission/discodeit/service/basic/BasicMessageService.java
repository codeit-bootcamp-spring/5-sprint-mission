package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  //
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public Message create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    String content = messageCreateRequest.content();

    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("Channel with id " + channelId + " does not exist");
    }
    if (!userRepository.existsById(authorId)) {
      throw new NoSuchElementException("Author with id " + authorId + " does not exist");
    }

    User author = userRepository.findById(authorId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + authorId + " does not exist"));
    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));


    List<UUID> attachmentIds = binaryContentCreateRequests.stream()
            .map(attachmentRequest -> {
              String fileName = attachmentRequest.fileName();
              String contentType = attachmentRequest.contentType();
              byte[] bytes = attachmentRequest.bytes();

              BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                      contentType);
              BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
              binaryContentStorage.put(binaryContent.getId(), bytes);
              return createdBinaryContent.getId();
            })
            .toList();

    List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(attachmentIds);
    for (UUID attachmentId : attachmentIds) {
      BinaryContent bc = binaryContentRepository.findById(attachmentId)
              .orElseThrow(() -> new NoSuchElementException("Attachment with id " + attachmentId + " does not exist"));
      binaryContents.add(bc);
    }

    Message message = new Message(
            content,
            channel,
            author,
            binaryContents
    );


    return messageRepository.save(message);
  }

  @Override
  public Message find(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId).stream()
        .toList();
  }

  public PageResponse<Message> findAllByChannelId(
          UUID channelId,
          @RequestParam(value = "cursor", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor, // ISO8601 문자열 → LocalDateTime
          Pageable pageable // Spring이 자동으로 page, size, sort 매핑)
  ){
    List<Message> messages;

    if (cursor == null) {
      // 첫 페이지: 최신 메시지 51개
      messages = messageRepository.findTop51ByChannelIdOrderByCreatedAtDesc(channelId);
    } else {
      // cursor 이전 메시지 51개
      messages = messageRepository.findTop51ByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(channelId, cursor);
    }

    boolean hasNext = messages.size() > 50;
    if (hasNext) {
      messages = messages.subList(0, 50); // 초과분 잘라내기
    }

    Object nextCursor = hasNext && !messages.isEmpty()
            ? messages.get(messages.size() - 1).getCreatedAt()
            : null;

    return new PageResponse<>(
            messages,
            nextCursor,
            pageable.getPageSize(),
            hasNext,
            null // totalElements는 필요 없으므로 null
    );





//    return messageRepository.findAllByChannelId(channelId).stream()
//            .toList();
  }

  @Override
  public Message update(UUID messageId, MessageUpdateRequest request) {
    String newContent = request.newContent();
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    message.update(newContent);
    return messageRepository.save(message);
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    List<BinaryContent> attachment=message.getAttachment();
    attachment.forEach(binaryContent-> {
                              binaryContentRepository.deleteById(binaryContent.getId());
    });


    messageRepository.deleteById(messageId);
  }
}
