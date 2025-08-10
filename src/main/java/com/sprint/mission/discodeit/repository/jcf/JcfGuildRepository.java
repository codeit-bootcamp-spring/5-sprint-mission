package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.entitydev.guild.DevGuild;
import com.sprint.mission.discodeit.repository.devrepository.DevGuildRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Repository
@Profile("test")
public class JcfGuildRepository extends JcfBaseRepository<DevGuild> implements DevGuildRepository {

    private static final Comparator<DevGuild> BY_NAME =
            Comparator.comparing(DevGuild::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

    @Override
    protected String getEntityTypeName() {
        return "Guild";
    }

    @Override
    public List<DevGuild> findDiscoverableGuilds() {
        return findAll().stream()
                .filter(DevGuild::isDiscoverable)
                .sorted(BY_NAME)
                .toList();
    }

    @Override
    public List<DevGuild> findGuildsOwnedByUser(UUID userId) {
        if (userId == null) return List.of();
        return findAll().stream()
                .filter(g -> g.isOwner(userId))
                .sorted(BY_NAME) // File과 동일하게 정렬
                .toList();
    }

    @Override
    public List<DevGuild> searchGuilds(String keyword) {
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
