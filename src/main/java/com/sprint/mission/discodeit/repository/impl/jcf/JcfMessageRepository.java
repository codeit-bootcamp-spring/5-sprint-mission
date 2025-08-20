package com.sprint.mission.discodeit.repository.impl.jcf;

import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class JcfMessageRepository extends AbstractJcfRepository<Message> implements
    MessageRepository {

  public JcfMessageRepository() {
    super(Message.class);
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    Objects.requireNonNull(channelId, "channelId must not be null");
    return findAll().stream()
        .filter(m -> channelId.equals(m.getChannelId()))
        .sorted(Comparator.comparing(Message::getCreatedAt))
        .toList();
  }

  @Override
  public List<Message> findRecentByChannelId(UUID channelId, int limit) {
    Objects.requireNonNull(channelId, "channelId must not be null");
    int lim = normalizeLimit(limit);
    if (lim == 0) {
      return List.of();
    }
    return findAll().stream()
        .filter(m -> channelId.equals(m.getChannelId()))
        .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
        .limit(lim)
        .sorted(Comparator.comparing(Message::getCreatedAt))
        .toList();
  }

  @Override
  public Map<UUID, Instant> findLastMessageAtByChannelIds(Set<UUID> channelIds) {
    if (channelIds == null || channelIds.isEmpty()) {
      return Map.of();
    }
    return data.values().stream()
        .filter(m -> !m.isDeleted())
        .filter(m -> channelIds.contains(m.getChannelId()))
        .filter(m -> m.getCreatedAt() != null)
        .collect(Collectors.toMap(
            Message::getChannelId,
            Message::getCreatedAt,
            BinaryOperator.maxBy(Comparator.naturalOrder())
        ));
  }

  @Override
  public List<Message> findAllByAuthorId(UUID authorId) {
    Objects.requireNonNull(authorId, "authorId must not be null");
    return findAll().stream()
        .filter(m -> authorId.equals(m.getAuthorId()))
        .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
        .toList();
  }

  @Override
  public List<Message> findAllReplies(UUID replyTo) {
    Objects.requireNonNull(replyTo, "replyTo must not be null");
    return findAll().stream()
        .filter(m -> replyTo.equals(m.getReplyTo()))
        .sorted(Comparator.comparing(Message::getCreatedAt))
        .toList();
  }

  @Override
  public List<Message> searchInChannel(UUID channelId, String keyword) {
    Objects.requireNonNull(channelId, "channelId must not be null");
    String k = MessageRepository.normalizeKeyword(keyword);
    if (k.isEmpty()) {
      return List.of();
    }
    return findAll().stream()
        .filter(m -> channelId.equals(m.getChannelId()))
        .filter(m -> {
          String c = m.getContent();
          return c != null && c.toLowerCase().contains(k);
        })
        .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
        .toList();
  }

  @Override
  public int softDeleteAllByChannelId(UUID channelId) {
    Objects.requireNonNull(channelId, "channelId must not be null");
    Set<UUID> ids = findAll().stream()
        .filter(m -> channelId.equals(m.getChannelId()))
        .map(Message::getId)
        .collect(Collectors.toSet());
    return softDeleteAllByIds(ids);
  }

  @Override
  public int softDeleteAllByAuthorId(UUID authorId) {
    Objects.requireNonNull(authorId, "authorId must not be null");
    Set<UUID> ids = findAll().stream()
        .filter(m -> authorId.equals(m.getAuthorId()))
        .map(Message::getId)
        .collect(Collectors.toSet());
    return softDeleteAllByIds(ids);
  }

  @Override
  public long countByChannelId(UUID channelId) {
    Objects.requireNonNull(channelId, "channelId must not be null");
    return findAll().stream().filter(m -> channelId.equals(m.getChannelId())).count();
  }
}
