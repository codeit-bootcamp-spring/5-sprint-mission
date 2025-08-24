package com.sprint.mission.discodeit.repository.impl.jcf;

import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
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
    return data.values().stream()
        .filter(Message::isNotDeleted)
        .filter(m -> channelId.equals(m.getChannelId()))
        .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
        .toList();
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
  public void deleteAllByChannelId(UUID channelId) {
    Objects.requireNonNull(channelId, "channelId must not be null");
    Set<UUID> ids = findAll().stream()
        .filter(m -> channelId.equals(m.getChannelId()))
        .map(Message::getId)
        .collect(Collectors.toSet());
    deleteAllByIdIn(ids);
  }

  @Override
  public void deleteAllByAuthorId(UUID authorId) {
    Objects.requireNonNull(authorId, "authorId must not be null");
    Set<UUID> ids = findAll().stream()
        .filter(m -> authorId.equals(m.getAuthorId()))
        .map(Message::getId)
        .collect(Collectors.toSet());
    deleteAllByIdIn(ids);
  }

  @Override
  public long countByChannelId(UUID channelId) {
    Objects.requireNonNull(channelId, "channelId must not be null");
    return findAll().stream().filter(m -> channelId.equals(m.getChannelId())).count();
  }
}
