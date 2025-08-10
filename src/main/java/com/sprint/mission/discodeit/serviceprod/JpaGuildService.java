package com.sprint.mission.discodeit.serviceprod;

import com.sprint.mission.discodeit.domain.entityprod.ProdChannel;
import com.sprint.mission.discodeit.domain.entityprod.ProdGuild;
import com.sprint.mission.discodeit.domain.enums.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Profile("prod")
public class JpaGuildService {
    public List<ProdGuild> findGuildsJoinedByUser(UUID userId) {
        return List.of();
    }

    public void deleteGuild(UUID guildId, UUID ownerId) {

    }

    public void updateName(UUID guildId, String name) {

    }

    public void updateDiscoverable(UUID guildId, boolean discoverable) {

    }

    public void updateOwnerId(UUID guildId, UUID oldOwnerId, UUID newOwnerId) {

    }

    public void addChannel(UUID guildId, ProdChannel jpaChannel) {

    }

    public void removeChannel(UUID guildId, ProdChannel jpaChannel) {

    }

    public List<ProdChannel> getChannels(UUID guildId) {
        return List.of();
    }

    public void addMember(UUID guildId, UUID userId) {

    }

    public void removeMember(UUID guildId, UUID userId) {

    }

    public boolean isMember(UUID guildId, UUID userId) {
        return false;
    }

    public List<UUID> getMemberIds(UUID guildId) {
        return List.of();
    }

    public int getMemberCount(UUID guildId) {
        return 0;
    }

    public Set<Permission> getMemberPermissions(UUID guildId, UUID userId) {
        return Set.of();
    }

    public void updateMemberPermissions(UUID guildId, UUID userId, Set<Permission> permissions) {

    }

    public void addBan(UUID guildId, UUID userId) {

    }

    public void removeBan(UUID guildId, UUID userId) {

    }

    public boolean isBanned(UUID guildId, UUID userId) {
        return false;
    }

    public Set<UUID> getBannedUsers(UUID guildId) {
        return Set.of();
    }

    public int getBanCount(UUID guildId) {
        return 0;
    }
}
