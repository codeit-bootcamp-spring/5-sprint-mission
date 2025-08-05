package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.enums.Permission;
import com.sprint.mission.discodeit.service.GuildService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class JcfGuildService extends BaseJcfService<Guild> implements GuildService {
    private static final JcfGuildService instance = new JcfGuildService();

    private UserService userService;

    private JcfGuildService() {
    }

    public static JcfGuildService getInstance() {
        return instance;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Guild save(Guild guild) {
        userService.getOrThrow(guild.getOwnerId());
        return super.save(guild);
    }

    @Override
    public List<Guild> findDiscoverableGuilds() {
        return findAll().stream().filter(Guild::isDiscoverable).toList();
    }

    @Override
    public List<Guild> findGuildsOwnedByUser(UUID userId) {
        userService.getOrThrow(userId);
        return findAll().stream().filter(g -> g.isOwner(userId)).toList();
    }

    @Override
    public List<Guild> findGuildsJoinedByUser(UUID id) {
        return userService.getGuilds(id).stream()
                .map(this::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public List<Guild> searchGuilds(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("검색어를 입력해주세요.");
        }

        return data.stream().filter(g -> !g.isDeleted() && g.getName().contains(keyword)).toList();
    }

    @Override
    public void deleteGuild(UUID guildId, UUID ownerId) {
        Guild guild = getOrThrow(guildId);

        if (!guild.isOwner(ownerId)) {
            throw new IllegalArgumentException("삭제할 권한이 없습니다.");
        }

        for (UUID userId : guild.getMembers().keySet()) {
            userService.removeGuild(userId, guildId);
        }

        deleteById(guildId);
    }

    @Override
    public void updateName(UUID guildId, String name) {
        update(guildId, g -> g.setName(name));
    }

    @Override
    public void updateDiscoverable(UUID guildId, boolean discoverable) {
        update(guildId, g -> g.setDiscoverable(discoverable));
    }

    @Override
    public void updateOwnerId(UUID guildId, UUID oldOwnerId, UUID newOwnerId) {
        userService.getOrThrow(oldOwnerId);
        userService.getOrThrow(newOwnerId);
        if (!getOrThrow(guildId).isOwner(oldOwnerId)) {
            throw new IllegalArgumentException("서버 주인 변경 권한이 없습니다.");
        }

        update(
                guildId,
                g -> {
                    if (g.isNotMember(newOwnerId)) {
                        throw new IllegalArgumentException("해당 유저는 이 서버의 멤버가 아닙니다.");
                    }
                    g.setOwnerId(newOwnerId);
                });
    }

    @Override
    public void addChannel(UUID guildId, Channel channel) {
        update(guildId, g -> g.addChannel(channel));
    }

    @Override
    public void removeChannel(UUID guildId, Channel channel) {
        update(guildId, g -> g.removeChannel(channel));
    }

    @Override
    public List<Channel> getChannels(UUID guildId) {
        Guild guild = getOrThrow(guildId);
        return guild.getChannels();
    }

    @Override
    public void addMember(UUID guildId, UUID member) {
        userService.getOrThrow(member);
        userService.addGuild(member, guildId);
        update(guildId, g -> g.addMember(member));
    }

    @Override
    public void removeMember(UUID guildId, UUID member) {
        if (getOrThrow(guildId).isOwner(member)) {
            throw new IllegalArgumentException("서버 주인 변경 후 퇴장이 가능합니다");
        }
        update(guildId, g -> g.removeMember(member));
    }

    @Override
    public boolean isMember(UUID guildId, UUID member) {
        Guild guild = getOrThrow(guildId);
        return guild.getMembers().containsKey(member);
    }

    @Override
    public List<UUID> getMemberIds(UUID guildId) {
        Guild guild = getOrThrow(guildId);
        return List.copyOf(guild.getMembers().keySet());
    }

    @Override
    public int getMemberCount(UUID guildId) {
        Guild guild = getOrThrow(guildId);
        return guild.getMembers().size();
    }

    @Override
    public Set<Permission> getMemberPermissions(UUID guildId, UUID member) {
        Guild guild = getOrThrow(guildId);
        Set<Permission> permissions = guild.getMembers().get(member);
        if (permissions == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(permissions);
    }

    @Override
    public void updateMemberPermissions(UUID guildId, UUID member, Set<Permission> permissions) {
        userService.getOrThrow(member);
        update(guildId, g -> g.updateMemberPermissions(member, permissions));
    }

    @Override
    public void addBan(UUID guildId, UUID userId) {
        userService.getOrThrow(userId);
        Guild guild = getOrThrow(guildId);
        if (guild.isBanned(userId)) {
            throw new IllegalStateException("이미 밴된 유저입니다.");
        }
        update(guildId, g -> g.addBan(userId));
    }

    @Override
    public void removeBan(UUID guildId, UUID userId) {
        update(guildId, g -> g.removeBan(userId));
    }

    @Override
    public boolean isBanned(UUID guildId, UUID userId) {
        Guild guild = getOrThrow(guildId);
        return guild.getBans().contains(userId);
    }

    @Override
    public Set<UUID> getBannedUsers(UUID guildId) {
        Guild guild = getOrThrow(guildId);
        return guild.getBans();
    }

    @Override
    public int getBanCount(UUID guildId) {
        Guild guild = getOrThrow(guildId);
        return guild.getBans().size();
    }
}
