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
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();
    log.info("메시지 생성 요청: channelId={}, authorId={}", channelId, authorId);

    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
              log.error("메시지 생성 실패 - 채널 없음: channelId={}", channelId);
              return new ChannelNotFoundException();
            });

    User author = userRepository.findById(authorId)
            .orElseThrow(() -> {
              log.error("메시지 생성 실패 - 작성자 없음: authorId={}", authorId);
              return new UserNotFoundException();
            });

    List<BinaryContent> attachments = binaryContentCreateRequests.stream()
            .map(attachmentRequest -> {
              log.debug("첨부파일 저장: fileName={}, size={}",
                      attachmentRequest.fileName(), attachmentRequest.bytes().length);
              BinaryContent binaryContent = new BinaryContent(
                      attachmentRequest.fileName(),
                      (long) attachmentRequest.bytes().length,
                      attachmentRequest.contentType()
              );
              binaryContentRepository.save(binaryContent);
              binaryContentStorage.put(binaryContent.getId(), attachmentRequest.bytes());
              return binaryContent;
            })
            .toList();

    Message message = new Message(
            messageCreateRequest.content(),
            channel,
            author,
            attachments
    );

    messageRepository.save(message);
    log.info("메시지 생성 성공: messageId={}", message.getId());

    return messageMapper.toDto(message);
  }

  @Transactional(readOnly = true)
  @Override
  public MessageDto find(UUID messageId) {
    log.info("메시지 조회 요청: messageId={}", messageId);
    return messageRepository.findById(messageId)
            .map(messageMapper::toDto)
            .orElseThrow(() -> {
              log.error("메시지 조회 실패 - 없음: messageId={}", messageId);
              return new MessageNotFoundException();
            });
  }

  @Transactional(readOnly = true)
  @Override
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createAt,
                                                     Pageable pageable) {
    log.info("채널 메시지 목록 조회 요청: channelId={}, cursor={}", channelId, createAt);

    Slice<MessageDto> slice = messageRepository.findAllByChannelIdWithAuthor(
                    channelId,
                    Optional.ofNullable(createAt).orElse(Instant.now()),
                    pageable)
            .map(messageMapper::toDto);

    Instant nextCursor = null;
    if (!slice.getContent().isEmpty()) {
      nextCursor = slice.getContent().get(slice.getContent().size() - 1).createdAt();
    }

    log.info("채널 메시지 목록 조회 완료: channelId={}, count={}", channelId, slice.getContent().size());
    return pageResponseMapper.fromSlice(slice, nextCursor);
  }

  @Transactional
  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    log.info("메시지 수정 요청: messageId={}", messageId);

    Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> {
              log.error("메시지 수정 실패 - 없음: messageId={}", messageId);
              return new MessageNotFoundException();
            });

    message.update(request.newContent());
    log.info("메시지 수정 성공: messageId={}", messageId);

    return messageMapper.toDto(message);
  }

  @Transactional
  @Override
  public void delete(UUID messageId) {
    log.info("메시지 삭제 요청: messageId={}", messageId);

    if (!messageRepository.existsById(messageId)) {
      log.error("메시지 삭제 실패 - 없음: messageId={}", messageId);
      throw new MessageNotFoundException();
    }

    messageRepository.deleteById(messageId);
    log.info("메시지 삭제 성공: messageId={}", messageId);
  }
}