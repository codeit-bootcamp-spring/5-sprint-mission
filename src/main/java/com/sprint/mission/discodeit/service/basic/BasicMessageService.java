package com.sprint.mission.discodeit.service.basic;

<<<<<<< HEAD
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
=======
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
<<<<<<< HEAD
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("basicMessageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;

  @Override
  public MessageResponseDto create(MessageCreateRequest request,
      List<BinaryContentCreateRequest> attachmentRequests) {

    if (!channelRepository.existsById(request.channelId())) {
      throw new NoSuchElementException("Channel not found with id " + request.channelId());
    }
    if (!userRepository.existsById(request.authorId())) {
      throw new NoSuchElementException("Author not found with id " + request.authorId());
    }

    List<UUID> attachmentIds = attachmentRequests.stream()
        .map(attachmentRequest -> {
          BinaryContent binaryContent = new BinaryContent(
              attachmentRequest.fileName(),
              (long) (attachmentRequest.bytes() != null ? attachmentRequest.bytes().length : 0),
              attachmentRequest.contentType(),
              attachmentRequest.bytes()
          );
          BinaryContent savedContent = binaryContentRepository.save(binaryContent);
          return savedContent.getId();
        })
        .toList();

    Message message = new Message(
        request.content(),
        request.channelId(),
        request.authorId(),
        attachmentIds
    );

    Message savedMessage = messageRepository.save(message);

    // DTO 로 반환
    return new MessageResponseDto(
        savedMessage.getId(),
        savedMessage.getContent(),
        savedMessage.getChannelId(),
        savedMessage.getAuthorId(),
        savedMessage.getAttachmentIds(),
        savedMessage.getCreatedAt(),
        savedMessage.getUpdatedAt()
    );
  }


  @Override
  public MessageResponseDto find(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    return new MessageResponseDto(
        message.getId(),
        message.getContent(),
        message.getChannelId(),
        message.getAuthorId(),
        message.getAttachmentIds(),
        message.getCreatedAt(),
        message.getUpdatedAt()
    );
  }

  @Override
  public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId)
        .stream()
        .map(message -> new MessageResponseDto(
            message.getId(),
            message.getContent(),
            message.getChannelId(),
            message.getAuthorId(),
            message.getAttachmentIds(),
            message.getCreatedAt(),
            message.getUpdatedAt()
        ))
        .toList();
  }


  @Override
  public MessageResponseDto update(UUID MessageId, MessageUpdateRequest request) {
    String newContent = request.newContent();
    Message message = messageRepository.findById(request.messageId())
        .orElseThrow(() -> new NoSuchElementException(
            "Message with id " + request.messageId() + " not found"));

    message.update(newContent);
    Message updatedMessage = messageRepository.save(message);

    return new MessageResponseDto(
        updatedMessage.getId(),
        updatedMessage.getContent(),
        updatedMessage.getChannelId(),
        updatedMessage.getAuthorId(),
        updatedMessage.getAttachmentIds(),
        updatedMessage.getCreatedAt(),
        updatedMessage.getUpdatedAt()
    );
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    message.getAttachmentIds()
        .forEach(binaryContentRepository::deleteById);

    messageRepository.deleteById(messageId);
  }


=======
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageMapper messageMapper;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final PageResponseMapper pageResponseMapper;

  @Transactional
  @Override
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    log.info("[MESSAGE][CREATE] channelId={}, authorId={}, files={}",
        messageCreateRequest.channelId(), messageCreateRequest.authorId(),
        binaryContentCreateRequests != null ? binaryContentCreateRequests.size() : 0);
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

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
        }).toList();

    String content = messageCreateRequest.content();
    Message message = new Message(content, channel, author, attachments);

    messageRepository.save(message);
    MessageDto dto = messageMapper.toDto(message);
    log.debug("[MESSAGE][CREATE][DONE] id={}", dto.id());
    return dto;
  }

  @Transactional(readOnly = true)
  @Override
  public MessageDto find(UUID messageId) {
    log.info("[MESSAGE][FIND] id={}", messageId);
    MessageDto dto = messageRepository.findById(messageId).map(messageMapper::toDto)
        .orElseThrow(() -> new MessageNotFoundException(messageId));
    log.debug("[MESSAGE][FIND][DONE] id={}", dto.id());
    return dto;
  }

  @Transactional(readOnly = true)
  @Override
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createAt,
      Pageable pageable) {
    log.info("[MESSAGE][FINDALL] channelId={}, createAt={}, pageable={}", channelId, createAt,
        pageable);
    Slice<MessageDto> slice = messageRepository.findAllByChannelIdWithAuthor(channelId,
        Optional.ofNullable(createAt).orElse(Instant.now()), pageable).map(messageMapper::toDto);

    Instant nextCursor = null;
    if (!slice.getContent().isEmpty()) {
      nextCursor = slice.getContent().get(slice.getContent().size() - 1).createdAt();
    }

    PageResponse<MessageDto> pageResponse = pageResponseMapper.fromSlice(slice, nextCursor);
    log.debug("[MESSAGE][FINDALL][DONE] pageResponse={}", pageResponse);
    return pageResponse;
  }

  @Transactional
  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    log.info("[MESSAGE][UPDATE] id={}, newContent={}", messageId, request.newContent());
    String newContent = request.newContent();
    Message message = messageRepository.findById(messageId).orElseThrow(
        () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    message.update(newContent);
    MessageDto dto = messageMapper.toDto(message);
    log.debug("[MESSAGE][UPDATE][DONE] id={}", dto.id());
    return dto;
  }

  @Transactional
  @Override
  public void delete(UUID messageId) {
    log.warn("[MESSAGE][DELETE] id={}", messageId);
    if (!messageRepository.existsById(messageId)) {
      throw new MessageNotFoundException(messageId);
    }
    messageRepository.deleteById(messageId);
    log.debug("[MESSAGE][DELETE][DONE] id={}", messageId);
  }
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
}
