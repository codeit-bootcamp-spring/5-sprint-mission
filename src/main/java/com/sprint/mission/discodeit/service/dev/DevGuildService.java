package com.sprint.mission.discodeit.service.dev;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entitydev.DevChannel;
import com.sprint.mission.discodeit.domain.entitydev.guild.DevGuild;
import com.sprint.mission.discodeit.domain.entitydev.guild.DevGuildPermissions;
import com.sprint.mission.discodeit.domain.enums.Permission;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DevGuildService {

    List<DevGuild> findGuildsJoinedByUser(UUID userId);

    void deleteGuild(UUID guildId, UUID ownerId);

    void updateName(UUID guildId, String name);

    void updateDiscoverable(UUID guildId, boolean discoverable);

    void updateOwnerId(UUID guildId, UUID oldOwnerId, UUID newOwnerId);

    void addChannel(UUID guildId, Channel channel);

    void removeChannel(UUID guildId, Channel channel);

    List<DevChannel> getChannels(UUID guildId);

    void addMember(UUID guildId, UUID userId);

    void removeMember(UUID guildId, UUID userId);

    boolean isMember(UUID guildId, UUID userId);

    List<UUID> getMemberIds(UUID guildId);

    int getMemberCount(UUID guildId);

    Set<DevGuildPermissions> getMemberPermissions(UUID guildId, UUID userId);

    void updateMemberPermissions(UUID guildId, UUID userId, Set<Permission> permissions);

    void addBan(UUID guildId, UUID userId);

    void removeBan(UUID guildId, UUID userId);

    boolean isBanned(UUID guildId, UUID userId);

    Set<UUID> getBannedUsers(UUID guildId);

    int getBanCount(UUID guildId);
}
