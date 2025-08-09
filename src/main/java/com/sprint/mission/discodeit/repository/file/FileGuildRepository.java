package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppStorageProperties;
import com.sprint.mission.discodeit.domain.deventity.guild.DevGuild;
import com.sprint.mission.discodeit.repository.devrepository.DevGuildRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Repository
@Profile("dev")
public class FileGuildRepository extends FileBaseRepository<DevGuild> implements DevGuildRepository {
    public FileGuildRepository(AppStorageProperties storageProperties) {
        super(DevGuild.class, storageProperties);
    }

    private static String lower(String s) {
        return s == null ? null : s.toLowerCase(Locale.ROOT);
    }

    private static final Comparator<DevGuild> BY_NAME =
            Comparator.comparing(DevGuild::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

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
                .filter(g -> userId.equals(g.getOwner()))
                .sorted(BY_NAME)
                .toList();
    }

    @Override
    public List<DevGuild> searchGuilds(String keyword) {
        String key = lower(keyword == null ? null : keyword.trim());
        if (key == null || key.isBlank()) return List.of();
        return findAll().stream()
                .filter(g -> g.getName() != null && lower(g.getName()).contains(key))
                .sorted(BY_NAME)
                .toList();
    }
}
