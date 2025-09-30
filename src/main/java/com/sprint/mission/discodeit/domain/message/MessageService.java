package com.sprint.mission.discodeit.domain.message;

import com.sprint.mission.discodeit.common.dto.response.PageResponse;
import com.sprint.mission.discodeit.common.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.domain.binarycontent.BinaryContentRepository;
import com.sprint.mission.discodeit.domain.binarycontent.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.domain.channel.ChannelRepository;
import com.sprint.mission.discodeit.domain.channel.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.domain.message.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.domain.message.dto.MessageDto;
import com.sprint.mission.discodeit.domain.message.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.domain.message.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.domain.message.mapper.MessageMapper;
import com.sprint.mission.discodeit.domain.user.UserRepository;
import com.sprint.mission.discodeit.domain.user.exception.UserNotFoundException;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageMapper messageMapper;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final PageResponseMapper pageResponseMapper;

  @Transactional
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();
    log.info("Creating new Message. 채널 아이디={}, 작성자 아이디={}", channelId, authorId );
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));
    User author = userRepository.findById(authorId)
        .orElseThrow(() -> new UserNotFoundException(authorId));


    List<BinaryContent> attachments = binaryContentCreateRequests.stream()
        .map(attachmentRequest -> {
          String fileName = attachmentRequest.fileName();
          String contentType = attachmentRequest.contentType();
          byte[] bytes = attachmentRequest.bytes();

          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;
        })
        .toList();

    String content = messageCreateRequest.content();
    Message message = new Message(
        content,
        channel,
        author,
        attachments
    );

    messageRepository.save(message);
    log.info("Created new Message. 채널 아이디={}, 작성자={}", message.getId(), message.getAuthor() );
    return messageMapper.toDto(message);
  }

  @Transactional(readOnly = true)
  public MessageDto find(UUID messageId) {
    return messageRepository.findById(messageId)
        .map(messageMapper::toDto)
        .orElseThrow(
            () -> new MessageNotFoundException(messageId));
  }

  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createAt,
      Pageable pageable) {
    Slice<MessageDto> slice = messageRepository.findAllByChannelIdWithAuthor(channelId,
            Optional.ofNullable(createAt).orElse(Instant.now()),
            pageable)
        .map(messageMapper::toDto);

    Instant nextCursor = null;
    if (!slice.getContent().isEmpty()) {
      nextCursor = slice.getContent().get(slice.getContent().size() - 1)
          .createdAt();
    }

    return pageResponseMapper.fromSlice(slice, nextCursor);
  }

  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    String newContent = request.newContent();
    log.info("Updating message. 메시지내용={}", newContent);
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new MessageNotFoundException(messageId));
    message.update(newContent);
    log.info("Updated message. 메시지내용={}", message.getContent());
    return messageMapper.toDto(message);
  }

  @Transactional
  public void delete(UUID messageId) {
    log.warn("Deleting Message. 아이디={}", messageId);
    if (!messageRepository.existsById(messageId)) {
      throw new MessageNotFoundException(messageId);
    }

    log.warn("Message deleted successfully. id={}", messageId);
    messageRepository.deleteById(messageId);
  }
}
