package com.sprint.mission.discodeit.service.jpa;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JpaMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final UserMapper userMapper;

  private static final int PAGE_SIZE = 50;

  @Override
  public MessageDto create(MessageCreateRequest request, List<BinaryContentCreateRequest> attachments) {
    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new NoSuchElementException("Channel not found"));
    User author = userRepository.findById(request.authorId())
        .orElseThrow(() -> new NoSuchElementException("Author not found"));

    List<BinaryContent> attachmentEntities = attachments.stream()
        .map(att -> binaryContentRepository.save(new BinaryContent(
            att.fileName(), (long) att.bytes().length, att.contentType())))
        .toList();

    Message message = new Message(request.content(), channel, author);
    message.getAttachments().addAll(attachmentEntities);

    return toDto(messageRepository.save(message));
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDto find(UUID messageId) {
    return messageRepository.findById(messageId)
        .map(this::toDto)
        .orElseThrow(() -> new NoSuchElementException("Message not found: " + messageId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<MessageDto> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelIdWithAuthorAndAttachments(channelId).stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("Message not found: " + messageId));
    message.update(request.newContent());
    return toDto(message);
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("Message not found: " + messageId));

    message.getAttachments().forEach(att -> binaryContentRepository.deleteById(att.getId()));
    messageRepository.delete(message);
  }

  private MessageDto toDto(Message message) {
    List<BinaryContentDto> attachments = message.getAttachments().stream()
        .map(binaryContentMapper::toDto)
        .collect(Collectors.toList());

    UserDto author = userMapper.toDto(message.getAuthor(), message.getAuthor().isOnline());

    return new MessageDto(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannel().getId(),
        author,
        attachments
    );
  }

  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findMessagesByChannelWithCursor(UUID channelId, java.time.Instant cursor) {
    List<Message> messages;
    if (cursor == null) {
      messages = messageRepository.findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(
          channelId,
          java.time.Instant.now(),
          PageRequest.of(0, PAGE_SIZE)
      );
    } else {
      messages = messageRepository.findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(
          channelId,
          cursor,
          PageRequest.of(0, PAGE_SIZE)
      );
    }

    List<MessageDto> dtos = messages.stream().map(this::toDto).toList();

    java.time.Instant nextCursor = dtos.isEmpty() ? null : dtos.get(dtos.size() - 1).createdAt();

    boolean hasNext = messages.size() == PAGE_SIZE;

    return new PageResponse<>(dtos, nextCursor, PAGE_SIZE, hasNext, null);
  }
}
