package com.sprint.mission.discodeit.repository.impl.file;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.entity.Guild;
import com.sprint.mission.discodeit.repository.GuildRepository;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

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
