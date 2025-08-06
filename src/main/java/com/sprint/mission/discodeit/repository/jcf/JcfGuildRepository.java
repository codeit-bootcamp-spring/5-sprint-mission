package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.repository.GuildRepository;

import java.util.List;
import java.util.UUID;

public class JcfGuildRepository extends BaseJcfRepository<Guild> implements GuildRepository {
    @Override
    public List<Guild> findDiscoverableGuilds() {
        return data.values().stream().filter(Guild::isDiscoverable).toList();
    }

    @Override
    public List<Guild> findGuildsOwnedByUser(UUID userId) {
        return data.values().stream().filter(g -> g.isOwner(userId)).toList();
    }

    @Override
    public List<Guild> searchGuilds(String keyword) {
        return data.values().stream().filter(g -> !g.isDeleted() && g.getName().contains(keyword)).toList();
    }
}
