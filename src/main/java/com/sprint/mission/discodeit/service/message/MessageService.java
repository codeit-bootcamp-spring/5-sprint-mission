package com.sprint.mission.discodeit.service.message;

import static com.sprint.mission.discodeit.mapper.MessageMapper.toMessageResponse;
import static com.sprint.mission.discodeit.mapper.MessageMapper.toMessageResponses;

import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.dto.request.message.MessageSendRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Comparator;
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

  @Transactional
  public MessageResponse send(UUID channelId, MessageSendRequest req) {
    Objects.requireNonNull(channelId, "channelId must not be null");
    channelRepository.getOrThrow(channelId);
    userRepository.getOrThrow(req.senderId());

    if (req.replyTo() != null) {
      Message parent = messageRepository.getOrThrow(req.replyTo());
      if (!parent.getChannelId().equals(channelId)) {
        throw new IllegalArgumentException("Reply 대상 메시지는 동일 채널이어야 합니다.");
      }
    }

    Message msg = new Message(
        channelId,
        req.senderId(),
        req.content(),
        (req.attachmentIds() == null ? Set.of() : req.attachmentIds()),
        req.replyTo()
    );

    Message saved = messageRepository.save(msg);
    return toMessageResponse(saved);
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

  public List<MessageResponse> findByChannel(UUID channelId, Integer page, Integer size) {
    Objects.requireNonNull(channelId, "channelId must not be null");
    channelRepository.getOrThrow(channelId);

    int p = (page == null || page < 0) ? 0 : page;
    int s = (size == null || size <= 0 || size > 200) ? 50 : size;

    List<Message> all = messageRepository.findAllByChannelId(channelId).stream()
        .filter(m -> !m.isDeleted())
        .sorted(Comparator.comparing(Message::getCreatedAt))
        .toList();

    int from = Math.min(p * s, all.size());
    int to = Math.min(from + s, all.size());

    return toMessageResponses(all.subList(from, to));
  }

  public MessageResponse find(UUID id) {
    return toMessageResponse(messageRepository.getOrThrow(id));
  }
}
