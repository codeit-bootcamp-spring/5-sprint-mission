package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageDto.CreateCommand;
import com.sprint.mission.discodeit.dto.MessageDto.UpdateCommand;
import com.sprint.mission.discodeit.dto.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;

  private final BinaryContentService binaryContentService;
  private final MessageMapper messageMapper;

  @Override
  @Transactional
  public MessageDto.Detail create(CreateCommand create) {

    User author = userRepository.findById(create.getAuthorId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
    Channel channel = channelRepository.findById(create.getChannelId())
                                       .orElseThrow(
                                           () -> new RuntimeException("Channel not found"));

    List<BinaryContent> contents = null;
    if (create.getAttachments() != null && !create.getAttachments()
                                                  .isEmpty()) {

      contents = create.getAttachments()
                       .stream()
                       .map(file -> {

                         return binaryContentService.create(
                             new BinaryContentDto.CreateCommand(file));
                       })
                       .toList();
    }

    Message message = messageRepository.save(
        messageMapper.toEntity(create, channel, author, contents));

    return messageMapper.toDetail(message);
  }

  @Override
  @Transactional
  public MessageDto.Detail update(UpdateCommand update) {

    Message message = messageRepository.findById(update.getId())
                                       .orElseThrow(
                                           () -> new RuntimeException("Message not found"));

    User author = userRepository.findById(message.getAuthor()
                                                 .getId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
    Channel channel = channelRepository.findById(message.getChannel()
                                                        .getId())
                                       .orElseThrow(
                                           () -> new RuntimeException("Channel not found"));

    message.update(update.getContent());

    return messageMapper.toDetail(message);
  }

  @Override
  public MessageDto.Detail findById(UUID id) {

    Message message = messageRepository.findById(id)
                                       .orElseThrow(
                                           () -> new RuntimeException("Message not found"));

    User author = userRepository.findById(message.getAuthor()
                                                 .getId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
    Channel channel = channelRepository.findById(message.getChannel()
                                                        .getId())
                                       .orElseThrow(
                                           () -> new RuntimeException("Channel not found"));

    return messageMapper.toDetail(message);
  }

  @Override
  public PageResponse<MessageDto.Detail> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable) {

    List<Message> messages = null;

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
                                       .orElseThrow(
                                           () -> new RuntimeException("Message not found"));

    messageRepository.delete(message);

    if (!message.getAttachments()
                .isEmpty()) {

      message.getAttachments()
             .forEach(a -> binaryContentService.delete(a.getId()));
    }
  }

  @Override
  @Transactional
  public void deleteAll() {
    messageRepository.deleteAll();
  }
}
