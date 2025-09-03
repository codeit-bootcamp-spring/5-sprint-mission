package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.neutral.MessageCreateCommand;
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
import jakarta.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
        .orElseThrow(() -> new NoSuchElementException("user not found :" + command.authorId()));
    Channel channel = channelRepository.findById(command.channelId())
        .orElseThrow(() -> new NoSuchElementException("channel not found :" + command.channelId()));

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
    return messageMapper.toDto(
        messageRepository.findById(messageId)
            .orElseThrow(
                () -> new NoSuchElementException(
                    "findById : 메세지를 찾을 수 없습니다. [" + messageId + "]")));
  }

  @Override
  @Transactional(readOnly = true)
  public List<MessageDto> findAllByChannelId(UUID channelId) {
    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("findAllByChannelId : 채널을 찾을 수 없습니다. [" + channelId + "]");
    }
    return messageRepository.findAll().stream()
        .filter(m -> m.getChannel().getId().equals(channelId))
        .map(messageMapper::toDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable) {
    return pageResponseMapper.fromPage(
        messageRepository.findAllByChannelId(channelId, pageable)
            .map(messageMapper::toDto));
  }


  @Override
  @Transactional
  public MessageDto update(UUID messageId, @Valid MessageUpdateRequest messageUpdateRequest) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("update : 메세지를 찾을 수 없습니다. [" + messageId + "]"));
    message.update(messageUpdateRequest.newContent());

    return messageMapper.toDto(messageRepository.save(message));
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("delete : 메세지를 찾을 수 없습니다. [" + messageId + "]"));

    for (BinaryContent binaryContent : message.getAttachments()) {
      binaryContentRepository.deleteById(binaryContent.getId());
    }
    messageRepository.deleteById(message.getId());
  }
}
