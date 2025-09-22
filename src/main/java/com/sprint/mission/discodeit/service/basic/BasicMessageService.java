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
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;  // 🔹 추가
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;      // 🔹 추가
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;// 🔹 추가
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
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
  public MessageDto create(
      MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {

    UUID channelId = messageCreateRequest.channelId();
    UUID authorId  = messageCreateRequest.authorId();
    log.info("[MSG][CREATE] channelId={} authorId={} attachments={}",
        channelId, authorId, binaryContentCreateRequests == null ? 0 : binaryContentCreateRequests.size());

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("[MSG][CREATE] channel not found channelId={}", channelId);
          return new ChannelNotFoundException(channelId);                // 🔹 교체
        });

    User author = userRepository.findById(authorId)
        .orElseThrow(() -> {
          log.warn("[MSG][CREATE] author not found authorId={}", authorId);
          return new UserNotFoundException(authorId);                    // 🔹 교체
        });

    List<BinaryContent> attachments = (binaryContentCreateRequests == null ? List.<BinaryContentCreateRequest>of() : binaryContentCreateRequests)
        .stream()
        .map(attachmentRequest -> {
          String fileName    = attachmentRequest.fileName();
          String contentType = attachmentRequest.contentType();
          byte[] bytes       = attachmentRequest.bytes();
          log.debug("[MSG][CREATE][ATTACH] name={} size={} contentType={}",
              fileName, bytes == null ? 0 : bytes.length, contentType);

          BinaryContent binaryContent = new BinaryContent(
              fileName, bytes == null ? 0L : (long) bytes.length, contentType);
          binaryContentRepository.save(binaryContent);
          if (bytes != null && bytes.length > 0) {
            binaryContentStorage.put(binaryContent.getId(), bytes);
          }
          return binaryContent;
        })
        .toList();

    String content = messageCreateRequest.content();
    Message message = new Message(content, channel, author, attachments);
    messageRepository.save(message);

    log.info("[MSG][CREATE][DONE] id={} channelId={} authorId={} attachments={}",
        message.getId(), channelId, authorId, attachments.size());
    return messageMapper.toDto(message);
  }

  @Transactional(readOnly = true)
  @Override
  public MessageDto find(UUID messageId) {
    log.debug("[MSG][FIND] id={}", messageId);
    return messageRepository.findById(messageId)
        .map(entity -> {
          log.info("[MSG][FIND][DONE] id={}", messageId);
          return messageMapper.toDto(entity);
        })
        .orElseThrow(() -> {
          log.warn("[MSG][FIND] not-found id={}", messageId);
          return new MessageNotFoundException(messageId);                // 🔹 교체
        });
  }

  @Transactional(readOnly = true)
  @Override
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createAt, Pageable pageable) {
    Instant cursor = Optional.ofNullable(createAt).orElse(Instant.now());
    log.debug("[MSG][FIND_ALL_BY_CHANNEL] channelId={} cursor={} page={} size={}",
        channelId, cursor, pageable.getPageNumber(), pageable.getPageSize());

    Slice<MessageDto> slice = messageRepository
        .findAllByChannelIdWithAuthor(channelId, cursor, pageable)
        .map(messageMapper::toDto);

    Instant nextCursor = null;
    if (!slice.getContent().isEmpty()) {
      nextCursor = slice.getContent().get(slice.getContent().size() - 1).createdAt();
    }

    log.info("[MSG][FIND_ALL_BY_CHANNEL][DONE] channelId={} fetched={} hasNext={} nextCursor={}",
        channelId, slice.getNumberOfElements(), slice.hasNext(), nextCursor);
    return pageResponseMapper.fromSlice(slice, nextCursor);
  }

  @Transactional
  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    String newContent = request.newContent();
    log.info("[MSG][UPDATE] id={}", messageId);

    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> {
          log.warn("[MSG][UPDATE] not-found id={}", messageId);
          return new MessageNotFoundException(messageId);                // 🔹 교체
        });

    message.update(newContent);
    log.info("[MSG][UPDATE][DONE] id={}", messageId);
    return messageMapper.toDto(message);
  }

  @Transactional
  @Override
  public void delete(UUID messageId) {
    log.info("[MSG][DELETE] id={}", messageId);

    if (!messageRepository.existsById(messageId)) {
      log.warn("[MSG][DELETE] not-found id={}", messageId);
      throw new MessageNotFoundException(messageId);                    // 🔹 교체
    }

    messageRepository.deleteById(messageId);
    log.info("[MSG][DELETE][DONE] id={}", messageId);
  }
}
