package com.sprint.mission.discodeit.repository.devrepository;

import com.sprint.mission.discodeit.domain.entitydev.guild.DevGuild;
import com.sprint.mission.discodeit.repository.BaseRepository;

import java.util.List;
import java.util.UUID;

public interface DevGuildRepository extends BaseRepository<DevGuild> {

    List<DevGuild> findDiscoverableGuilds();

    List<DevGuild> findGuildsOwnedByUser(UUID userId);

    List<DevGuild> searchGuilds(String keyword);
}
