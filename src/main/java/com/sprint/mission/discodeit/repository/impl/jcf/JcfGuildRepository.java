package com.sprint.mission.discodeit.repository.impl.jcf;

import com.sprint.mission.discodeit.domain.entity.Guild;
import com.sprint.mission.discodeit.repository.GuildRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
@Profile("test")
public class JcfGuildRepository extends AbstractJcfRepository<Guild> implements GuildRepository {

    @Override
    protected String getEntityTypeName() {
        return "Guild";
    }

    @Override
    public List<Guild> findDiscoverableGuilds() {
        return findAll().stream().filter(Guild::isDiscoverable).toList();
    }

    @Override
    public List<Guild> findGuildsOwnedByUser(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        return findAll().stream().filter(g -> userId.equals(g.getOwnerId())).toList();
    }

    @Override
    public List<Guild> findGuildsByMember(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        return findAll().stream().filter(g -> g.getUserIds().contains(userId)).toList();
    }

    @Override
    public List<Guild> searchGuilds(String keyword) {
        Objects.requireNonNull(keyword, "keyword must not be null");
        String k = keyword.strip().toLowerCase();
        if (k.isEmpty()) return List.of();
        return findAll().stream()
                .filter(Guild::isDiscoverable)
                .filter(g -> {
                    String name = g.getName();
                    return name != null && name.toLowerCase().contains(k);
                })
                .toList();
    }
}
