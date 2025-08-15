package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.entity.GuildPermissions;
import com.sprint.mission.discodeit.domain.enums.Permission;
import com.sprint.mission.discodeit.repository.GuildPermissionsRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Profile("dev")
public class FileGuildPermissionsRepository extends FileBaseRepository<GuildPermissions> implements GuildPermissionsRepository {

    public FileGuildPermissionsRepository(AppProperties appProperties) {
        super(GuildPermissions.class, appProperties.storage());
    }

    @Override
    public boolean existsByGuildIdAndUserId(UUID guildId, UUID userId) {
        Objects.requireNonNull(guildId, "guildId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        return findAll().stream().anyMatch(gp -> guildId.equals(gp.getGuildId()) && userId.equals(gp.getUserId()));
    }

    @Override
    public Optional<GuildPermissions> findByGuildIdAndUserId(UUID guildId, UUID userId) {
        Objects.requireNonNull(guildId, "guildId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        return findAll().stream().filter(gp -> guildId.equals(gp.getGuildId()) && userId.equals(gp.getUserId())).findFirst();
    }

    @Override
    public List<GuildPermissions> findAllByGuildId(UUID guildId) {
        Objects.requireNonNull(guildId, "guildId must not be null");
        return findAll().stream().filter(gp -> guildId.equals(gp.getGuildId())).toList();
    }

    @Override
    public List<GuildPermissions> findAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        return findAll().stream().filter(gp -> userId.equals(gp.getUserId())).toList();
    }

    @Override
    public List<GuildPermissions> findAllByGuildIdAndUserIdIn(UUID guildId, Collection<UUID> userIds) {
        Objects.requireNonNull(guildId, "guildId must not be null");
        Objects.requireNonNull(userIds, "userIds must not be null");
        if (userIds.isEmpty()) return List.of();
        Set<UUID> set = new HashSet<>(userIds);
        return findAll().stream()
                .filter(gp -> guildId.equals(gp.getGuildId()) && set.contains(gp.getUserId()))
                .toList();
    }

    @Override
    public List<GuildPermissions> findAllByGuildIdAndPermission(UUID guildId, Permission permission) {
        Objects.requireNonNull(guildId, "guildId must not be null");
        Objects.requireNonNull(permission, "permission must not be null");
        return findAll().stream()
                .filter(gp -> guildId.equals(gp.getGuildId()) && gp.getPermissions().contains(permission))
                .toList();
    }

    @Override
    public boolean softDeleteByGuildIdAndUserId(UUID guildId, UUID userId) {
        return findByGuildIdAndUserId(guildId, userId).map(e -> softDeleteById(e.getId())).orElse(false);
    }

    @Override
    public int softDeleteAllByGuildId(UUID guildId) {
        Objects.requireNonNull(guildId, "guildId must not be null");
        Set<UUID> ids = findAll().stream()
                .filter(gp -> guildId.equals(gp.getGuildId()))
                .map(GuildPermissions::getId)
                .collect(Collectors.toSet());
        return softDeleteAllByIds(ids);
    }

    @Override
    public int softDeleteAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        Set<UUID> ids = findAll().stream()
                .filter(gp -> userId.equals(gp.getUserId()))
                .map(GuildPermissions::getId)
                .collect(Collectors.toSet());
        return softDeleteAllByIds(ids);
    }
}
