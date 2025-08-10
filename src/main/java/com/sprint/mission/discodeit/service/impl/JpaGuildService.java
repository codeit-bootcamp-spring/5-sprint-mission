package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.guild.Guild;
import com.sprint.mission.discodeit.domain.enums.Permission;
import com.sprint.mission.discodeit.service.GuildService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Profile("prod")
@Transactional
public class JpaGuildService implements GuildService {
    @Override
    public List<Guild> findGuildsJoinedByUser(UUID userId) {
        return List.of();
    }

    @Override
    public void deleteGuild(UUID guildId, UUID ownerId) {

    }

    @Override
    public void updateName(UUID guildId, String name) {

    }

    @Override
    public void updateDiscoverable(UUID guildId, boolean discoverable) {

    }

    @Override
    public void updateOwnerId(UUID guildId, UUID oldOwnerId, UUID newOwnerId) {

    }

    @Override
    public void addChannel(UUID guildId, Channel channel) {

    }

    @Override
    public void removeChannel(UUID guildId, Channel channel) {

    }

    @Override
    public List<Channel> getChannels(UUID guildId) {
        return List.of();
    }

    @Override
    public void addMember(UUID guildId, UUID userId) {

    }

    @Override
    public void removeMember(UUID guildId, UUID userId) {

    }

    @Override
    public boolean isMember(UUID guildId, UUID userId) {
        return false;
    }

    @Override
    public List<UUID> getMemberIds(UUID guildId) {
        return List.of();
    }

    @Override
    public int getMemberCount(UUID guildId) {
        return 0;
    }

    @Override
    public Set<Permission> getMemberPermissions(UUID guildId, UUID userId) {
        return Set.of();
    }

    @Override
    public void updateMemberPermissions(UUID guildId, UUID userId, Set<Permission> permissions) {

    }

    @Override
    public void addBan(UUID guildId, UUID userId) {

    }

    @Override
    public void removeBan(UUID guildId, UUID userId) {

    }

    @Override
    public boolean isBanned(UUID guildId, UUID userId) {
        return false;
    }

    @Override
    public Set<UUID> getBannedUsers(UUID guildId) {
        return Set.of();
    }

    @Override
    public int getBanCount(UUID guildId) {
        return 0;
    }
}
