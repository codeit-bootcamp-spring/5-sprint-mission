package com.sprint.mission.discodeit.repository.impl.file;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@Profile("dev")
public class FileMessageRepository extends AbstractFileRepository<Message> implements
    MessageRepository {

  public FileMessageRepository(AppProperties appProperties) {
    super(Message.class, appProperties.storage());
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    try (Stream<Path> s = streamSerializedFiles()) {
      return s.map(this::readObject).flatMap(Optional::stream)
          .filter(Message::isNotDeleted)
          .filter(m -> channelId.equals(m.getChannelId()))
          .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
          .toList();
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
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
    var ids = findAll().stream()
        .filter(m -> channelId.equals(m.getChannelId()))
        .map(Message::getId)
        .collect(java.util.stream.Collectors.toSet());
    deleteAllByIdIn(ids);
  }

  @Override
  public void deleteAllByAuthorId(UUID authorId) {
    Objects.requireNonNull(authorId, "authorId must not be null");
    var ids = findAll().stream()
        .filter(m -> authorId.equals(m.getAuthorId()))
        .map(Message::getId)
        .collect(java.util.stream.Collectors.toSet());
    deleteAllByIdIn(ids);
  }

  @Override
  public long countByChannelId(UUID channelId) {
    Objects.requireNonNull(channelId, "channelId must not be null");
    return findAll().stream().filter(m -> channelId.equals(m.getChannelId())).count();
  }
}
