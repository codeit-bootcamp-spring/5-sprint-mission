package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.entity.guild.Guild;
import com.sprint.mission.discodeit.repository.GuildRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Repository
@Profile("test")
public class JcfGuildRepository extends JcfBaseRepository<Guild> implements GuildRepository {

    private static final Comparator<Guild> BY_NAME =
            Comparator.comparing(Guild::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

    @Override
    protected String getEntityTypeName() {
        return "Guild";
    }

    @Override
    public List<Guild> findDiscoverableGuilds() {
        return findAll().stream()
                .filter(Guild::isDiscoverable)
                .sorted(BY_NAME)
                .toList();
    }

    @Override
    public List<Guild> findGuildsOwnedByUser(UUID userId) {
        if (userId == null) return List.of();
        return findAll().stream()
                .filter(g -> g.isOwner(userId))
                .sorted(BY_NAME) // File과 동일하게 정렬
                .toList();
    }

    @Override
    public List<Guild> searchGuilds(String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();
        String q = keyword.trim().toLowerCase(Locale.ROOT);
        return findAll().stream()
                .filter(g -> {
                    String name = g.getName();
                    return name != null && name.toLowerCase(Locale.ROOT).contains(q);
                })
                .sorted(BY_NAME)
                .toList();
    }
}
