package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.Guild;
import java.util.List;
import java.util.UUID;

public interface GuildRepository extends AbstractRepository<Guild> {

  List<Guild> findDiscoverableGuilds();

  List<Guild> findGuildsByMember(UUID userId);

  void deleteAllByOwnerId(UUID ownerId);

  List<Guild> searchGuilds(String keyword);
}
