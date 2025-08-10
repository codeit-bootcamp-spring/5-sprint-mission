package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppStorageProperties;
import com.sprint.mission.discodeit.domain.entity.guild.Guild;
import com.sprint.mission.discodeit.repository.GuildRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Repository
@Profile("dev")
public class FileGuildRepository extends FileBaseRepository<Guild> implements GuildRepository {
    public FileGuildRepository(AppStorageProperties storageProperties) {
        super(Guild.class, storageProperties);
    }

    private static String lower(String s) {
        return s == null ? null : s.toLowerCase(Locale.ROOT);
    }

    private static final Comparator<Guild> BY_NAME =
            Comparator.comparing(Guild::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

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
                .filter(g -> userId.equals(g.getOwner()))
                .sorted(BY_NAME)
                .toList();
    }

    @Override
    public List<Guild> searchGuilds(String keyword) {
        String key = lower(keyword == null ? null : keyword.trim());
        if (key == null || key.isBlank()) return List.of();
        return findAll().stream()
                .filter(g -> g.getName() != null && lower(g.getName()).contains(key))
                .sorted(BY_NAME)
                .toList();
    }
}
