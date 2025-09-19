package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.neutral.MessageCreateCommand;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service("messageService")
@RequiredArgsConstructor
@Validated
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  @Transactional
  public MessageDto create(@Valid MessageCreateCommand command) {
    String content = command.content();
    User author = userRepository.findById(command.authorId())
        .orElseThrow(() -> UserNotFoundException.withDetail("author", command.authorId()));
    Channel channel = channelRepository.findById(command.channelId())
        .orElseThrow(() -> ChannelNotFoundException.withDetail("channel", command.channelId()));

    List<BinaryContent> attachments = command.attachments().stream()
        .map(request -> {
          BinaryContent binaryContent = new BinaryContent(
              request.fileName(),
              request.contentType(),
              request.bytes().length
          );
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), request.bytes());
          return binaryContent;
        })
        .toList();

    Message message = new Message(content, channel, author, attachments);

    return messageMapper.toDto(messageRepository.save(message));
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDto findById(UUID messageId) {
    return messageMapper.toDto(validateId(messageId));
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable) {

    if (!channelRepository.existsById(channelId)) {
      throw ChannelNotFoundException.withDetail("channel", channelId);
    }

    Slice<MessageDto> slice = (cursor == null)
        ? messageRepository.findAllByChannelIdOrderByCreatedAtDescIdDesc(channelId, pageable)
        .map(messageMapper::toDto)
        : messageRepository.findNextPage(channelId, cursor, channelId, pageable)
            .map(messageMapper::toDto);

    Instant nextCursor = (slice.hasNext() && slice.hasContent())
        ? slice.getContent().get(slice.getContent().size() - 1).createdAt()
        : null;

    return pageResponseMapper.fromSlice(slice, nextCursor);
  }


  @Override
  @Transactional
  public MessageDto update(UUID messageId, @Valid MessageUpdateRequest messageUpdateRequest) {
    Message message = validateId(messageId);
    message.update(messageUpdateRequest.newContent());

    return messageMapper.toDto(messageRepository.save(message));
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    Message message = validateId(messageId);

    for (BinaryContent binaryContent : message.getAttachments()) {
      binaryContentRepository.deleteById(binaryContent.getId());
    }
    messageRepository.deleteById(message.getId());
  }

  private Message validateId(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(() -> MessageNotFoundException.withDetail("id", messageId));
  }
}
