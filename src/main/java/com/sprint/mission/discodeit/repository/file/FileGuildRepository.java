package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.repository.GuildRepository;

import java.util.List;
import java.util.UUID;

public class FileGuildRepository extends BaseFileRepository<Guild> implements GuildRepository {
    public FileGuildRepository() {
        super(Guild.class);
    }

    @Override
    public List<Guild> findDiscoverableGuilds() {
        return List.of();
    }

    @Override
    public List<Guild> findGuildsOwnedByUser(UUID userId) {
        return List.of();
    }

    @Override
    public List<Guild> searchGuilds(String keyword) {
        return List.of();
    }
}