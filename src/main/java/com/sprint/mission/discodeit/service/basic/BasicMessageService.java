package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageDto.CreateCommand;
import com.sprint.mission.discodeit.dto.MessageDto.UpdateCommand;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;

  private final ApplicationEventPublisher publisher;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;

  @Override
  @Transactional
  public MessageDto.Detail create(CreateCommand create) {

    User author = userRepository.findById(create.getAuthorId())
                                .orElseThrow(() -> new UserNotFoundException(create.getAuthorId()));
    Channel channel = channelRepository.findById(create.getChannelId())
                                       .orElseThrow(() -> new ChannelNotFoundException(
                                           create.getChannelId()));

    List<BinaryContent> contents = null;
    if (create.getAttachments() != null && !create.getAttachments()
                                                  .isEmpty()) {

      contents = create.getAttachments()
                       .stream()
                       .map(file -> {

                         BinaryContent content = binaryContentRepository.save(
                             BinaryContent.builder()
                                          .size(file.getSize())
                                          .contentType(file.getContentType())
                                          .fileName(file.getName())
                                          .build());
                         try {

                           publisher.publishEvent(BinaryContentCreatedEvent.builder()
                                                                           .userId(author.getId())
                                                                           .binaryContentId(
                                                                               content.getId())
                                                                           .bytes(file.getBytes())
                                                                           .fileName(file.getName())
                                                                           .contentType(
                                                                               file.getContentType())
                                                                           .build());
                         } catch (Exception e) {
                           log.error("BinaryContent create error: {}", e.getMessage());
                         }

                         return content;
                       })
                       .toList();
    }

    Message message = messageRepository.save(
        messageMapper.toEntity(create, channel, author, contents));

    publisher.publishEvent(MessageCreatedEvent.builder()
                                              .messageId(message.getId())
                                              .channelId(message.getChannel()
                                                                .getId())
                                              .authorId(message.getAuthor()
                                                               .getId())
                                              .title(String.format("%s (#%s)", message.getAuthor()
                                                                                      .getUsername(),
                                                  message.getChannel()
                                                         .getName()))
                                              .content(message.getContent())
                                              .build());

    log.info("Message {} created", message.getId());

    return messageMapper.toDetail(message);
  }

  @Override
  @Transactional
  public MessageDto.Detail update(UpdateCommand update) {

    Message message = messageRepository.findById(update.getId())
                                       .orElseThrow(
                                           () -> new MessageNotFoundException(update.getId()));

    message.update(update.getContent());

    log.info("Message {} updated", message.getId());

    return messageMapper.toDetail(message);
  }

  @Override
  public MessageDto.Detail findById(UUID id) {

    Message message = messageRepository.findById(id)
                                       .orElseThrow(() -> new MessageNotFoundException(id));

    return messageMapper.toDetail(message);
  }

  @Override
  public PageResponse<MessageDto.Detail> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable) {

    List<Message> messages;

    if (cursor == null) {
      messages = messageRepository.findByChannelIdOrderByCreatedAtDesc(channelId, pageable);
    } else {
      messages = messageRepository.findByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(channelId,
          cursor, pageable);
    }

    List<MessageDto.Detail> result = messages.stream()
                                             .map(messageMapper::toDetail)
                                             .toList();

    int size = pageable.getPageSize();
    boolean hasNext = messages.size() == size;
    Object nextCursor = hasNext ? messages.get(messages.size() - 1)
                                          .getCreatedAt() : null;
    long totalElements = messageRepository.countByChannelId(channelId);

    return PageResponse.of(result, nextCursor, size, hasNext, totalElements);
  }

  @Override
  @Transactional
  public void delete(UUID id) {

    Message message = messageRepository.findById(id)
                                       .orElseThrow(() -> new MessageNotFoundException(id));

    messageRepository.delete(message);

    log.info("Message {} deleted", message.getId());
  }

  @Override
  @Transactional
  public void deleteAll() {
    messageRepository.deleteAll();
  }

  @Override
  public boolean isAuthor(UUID messageId, UUID userId) {

    if (userId == null) {
      return false;
    }

    return messageRepository.findById(messageId)
                            .map(msg -> msg.getAuthor()
                                           .getId()
                                           .equals(userId))
                            .orElse(false);
  }
}