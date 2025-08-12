package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.Guild;

import java.util.List;
import java.util.UUID;

public interface GuildRepository extends BaseRepository<Guild> {

    List<Guild> findDiscoverableGuilds();

    List<Guild> findGuildsOwnedByUser(UUID userId);

    List<Guild> findGuildsByMember(UUID userId);

    List<Guild> searchGuilds(String keyword);
}
