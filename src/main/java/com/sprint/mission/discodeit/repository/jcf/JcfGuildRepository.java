package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.deventity.guild.DevGuild;
import com.sprint.mission.discodeit.repository.devrepository.DevGuildRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Profile("test")
public class JcfGuildRepository extends JcfBaseRepository<DevGuild> implements DevGuildRepository {

    @Override
    protected String getEntityTypeName() {
        return "Guild";
    }

    @Override
    public List<DevGuild> findDiscoverableGuilds() {
        return data.values().stream().filter(DevGuild::isDiscoverable).toList();
    }

    @Override
    public List<DevGuild> findGuildsOwnedByUser(UUID userId) {
        return data.values().stream().filter(g -> g.isOwner(userId)).toList();
    }

    @Override
    public List<DevGuild> searchGuilds(String keyword) {
        return data.values().stream().filter(g -> !g.isDeleted() && g.getName().contains(keyword)).toList();
    }
}
