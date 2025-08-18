package com.sprint.mission.discodeit.service.message;

import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;


  public List<MessageResponse> findAllByChannelId(UUID channelId) {
    channelRepository.getOrThrow(channelId);

    return messageRepository.findAllByChannelId(channelId).stream()
        .map(MessageResponse::from)
        .toList();
  }

  @Transactional
  public MessageResponse create(MessageCreateRequest req, Set<UUID> attachmentIds) {
    channelRepository.getOrThrow(req.channelId());
    userRepository.getOrThrow(req.authorId());

    return MessageResponse.from(messageRepository.save(
        new Message(
            req.channelId(),
            req.authorId(),
            req.content(),
            attachmentIds
        )
    ));
  }

  @Transactional
  public void update(UUID messageId, MessageUpdateRequest req) {
    Message m = messageRepository.getOrThrow(messageId);
    if (!m.getAuthorId().equals(req.senderId())) {
      throw new IllegalStateException("작성자만 메시지를 수정할 수 있습니다.");
    }
    if (req.content() != null) {
      m.setContent(req.content());
    }
    if (req.attachmentIds() != null) {
      m.setAttachmentIds(req.attachmentIds());
    }
    messageRepository.save(m);
  }

  @Transactional
  public void delete(UUID messageId, UUID actorId) {
    Message m = messageRepository.getOrThrow(messageId);
    if (!m.getAuthorId().equals(Objects.requireNonNull(actorId, "actorId must not be null"))) {
      throw new IllegalStateException("작성자만 메시지를 삭제할 수 있습니다.");
    }
    messageRepository.softDeleteById(messageId);
  }

  public MessageResponse find(UUID id) {
    return MessageResponse.from(messageRepository.getOrThrow(id));
  }
}
