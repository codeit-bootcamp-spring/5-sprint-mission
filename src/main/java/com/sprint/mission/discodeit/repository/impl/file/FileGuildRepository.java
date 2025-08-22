package com.sprint.mission.discodeit.repository.impl.file;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.entity.Guild;
import com.sprint.mission.discodeit.repository.GuildRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
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
public class FileGuildRepository extends AbstractFileRepository<Guild> implements GuildRepository {

  public FileGuildRepository(AppProperties appProperties) {
    super(Guild.class, appProperties.storage());
  }

  @Override
  public List<Guild> findDiscoverableGuilds() {
    return findAll().stream()
        .filter(Guild::isDiscoverable)
        .toList();
  }

  @Override
  public List<Guild> findGuildsByMember(UUID userId) {
    Objects.requireNonNull(userId, "userId must not be null");
    return findAll().stream()
        .filter(g -> g.getUserIds().contains(userId))
        .toList();
  }

  @Override
  public void softDeleteAllByOwnerId(UUID ownerId) {
    Objects.requireNonNull(ownerId, "ownerId must not be null");
    try (Stream<Path> s = streamSerializedFiles()) {
      s.map(this::readObject).flatMap(Optional::stream)
          .filter(Guild::isNotDeleted)
          .filter(g -> g.isOwner(ownerId))
          .forEach(g -> {
            g.delete();
            save(g);
          });
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
  }

  @Override
  public List<Guild> searchGuilds(String keyword) {
    Objects.requireNonNull(keyword, "keyword must not be null");
    String k = keyword.strip().toLowerCase(Locale.ROOT);
    if (k.isEmpty()) {
      return List.of();
    }
    return findAll().stream()
        .filter(Guild::isDiscoverable)
        .filter(g -> {
          String name = g.getName();
          return name != null && name.toLowerCase(Locale.ROOT).contains(k);
        })
        .toList();
  }
}
