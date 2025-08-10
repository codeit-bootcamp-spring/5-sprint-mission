package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.Guild;
import com.sprint.mission.discodeit.domain.entity.GuildPermissions;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entityprod.ProdChannel;
import com.sprint.mission.discodeit.domain.enums.Permission;
import com.sprint.mission.discodeit.dto.request.GuildCreateCommand;
import com.sprint.mission.discodeit.dto.response.GuildResponse;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.GuildRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.util.Validators;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Profile({"test", "dev"})
public class BasicGuildService {

    private final GuildRepository guildRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    private GuildResponse toResponse(Guild g) {
        return new GuildResponse(
                g.getId(),
                g.getName(),
                g.isDiscoverable(),
                g.getOwnerId(),
                g.getUserIds(),
                g.getPermissions(),
                g.getChannelIds(),
                g.getBannedUserIds()
        );
    }

    private void update(UUID id, Consumer<Guild> updater) {
        Guild g = guildRepository.getOrThrow(id);
        updater.accept(g);
        guildRepository.save(g);
    }

    public GuildResponse create(GuildCreateCommand cmd) {
        Objects.requireNonNull(cmd, "cmd must not be null");
        Objects.requireNonNull(cmd.ownerId(), "ownerId must not be null");
        String n = Validators.validateGuildName(cmd.name());

        Guild saved = guildRepository.save(new Guild(
                n, cmd.discoverable(), cmd.ownerId()
        ));

        return toResponse(saved);
    }

    public List<Guild> findGuildsJoinedByUser(UUID userId) {
        Set<UUID> ids = userRepository.getOrThrow(userId).getGuildIds();
        return guildRepository.findAllByIds(ids);
    }

    public void deleteGuild(UUID guildId, UUID ownerId) {
        Guild guild = guildRepository.getOrThrow(guildId);
        if (!guild.isOwner(ownerId)) {
            throw new IllegalArgumentException("삭제할 권한이 없습니다.");
        }
        for (UUID uid : new HashSet<>(guild.getUserIds())) {
            User user = userRepository.getOrThrow(uid);
            user.leaveGuild(guildId);
            userRepository.save(user);
        }
        guildRepository.deleteById(guildId);
    }

    public void updateName(UUID guildId, String name) {
        update(guildId, g -> g.setName(name));
    }

    public void updateDiscoverable(UUID guildId, boolean discoverable) {
        update(guildId, g -> g.setDiscoverable(discoverable));
    }

    public void updateOwnerId(UUID guildId, UUID oldOwnerId, UUID newOwnerId) {
        userRepository.getOrThrow(oldOwnerId);
        userRepository.getOrThrow(newOwnerId);
        Guild guild = guildRepository.getOrThrow(guildId);
        if (!guild.isOwner(oldOwnerId)) {
            throw new IllegalArgumentException("서버 주인 변경 권한이 없습니다.");
        }
        if (guild.isNotMember(newOwnerId)) {
            throw new IllegalArgumentException("해당 유저는 이 서버의 멤버가 아닙니다.");
        }
        guild.setOwnerId(newOwnerId);
        guildRepository.save(guild);
    }

    public void addChannel(UUID guildId, ProdChannel jpaChannel) {
        Objects.requireNonNull(jpaChannel, "channel must not be null");
        channelRepository.getOrThrow(jpaChannel.getId());
        update(guildId, g -> g.addChannel(jpaChannel.getId()));
    }

    public void removeChannel(UUID guildId, ProdChannel jpaChannel) {
        Objects.requireNonNull(jpaChannel, "channel must not be null");
        update(guildId, g -> g.removeChannel(jpaChannel.getId()));
    }

    public List<Channel> getChannels(UUID guildId) {
        Set<UUID> ids = guildRepository.getOrThrow(guildId).getChannelIds();
        return channelRepository.findAllByIds(ids);
    }

    public void addMember(UUID guildId, UUID userId) {
        userRepository.getOrThrow(userId);
        Guild guild = guildRepository.getOrThrow(guildId);
        User user = userRepository.getOrThrow(userId);
        user.joinGuild(guildId);
        userRepository.save(user);

        guild.addUser(userId);
        guildRepository.save(guild);
    }

    public void removeMember(UUID guildId, UUID userId) {
        Guild guild = guildRepository.getOrThrow(guildId);
        if (guild.isOwner(userId)) {
            throw new IllegalArgumentException("서버 주인 변경 후 퇴장이 가능합니다.");
        }
        User user = userRepository.getOrThrow(userId);
        user.leaveGuild(guildId);
        userRepository.save(user);

        guild.removeUser(userId);
        guildRepository.save(guild);
    }

    public boolean isMember(UUID guildId, UUID userId) {
        return !guildRepository.getOrThrow(guildId).isNotMember(userId);
    }

    public List<UUID> getMemberIds(UUID guildId) {
        return List.copyOf(guildRepository.getOrThrow(guildId).getUserIds());
    }

    public int getMemberCount(UUID guildId) {
        return guildRepository.getOrThrow(guildId).getUserIds().size();
    }

    public Set<GuildPermissions> getMemberPermissions(UUID guildId, UUID userId) {
        // 인터페이스가 Set을 반환하므로 해당 유저의 권한 엔티티(0 또는 1개)를 Set으로 반환
        return guildRepository.getOrThrow(guildId).getPermissions().stream()
                .filter(p -> p.getUserId().equals(userId))
                .collect(Collectors.toSet());
    }

    public void updateMemberPermissions(UUID guildId, UUID userId, Set<Permission> permissions) {
        Guild guild = guildRepository.getOrThrow(guildId);
        if (guild.isNotMember(userId)) {
            throw new IllegalArgumentException("해당 유저는 이 서버의 멤버가 아닙니다.");
        }
        guild.setPermissions(userId, permissions);
        guildRepository.save(guild);
    }

    public void addBan(UUID guildId, UUID userId) {
        userRepository.getOrThrow(userId);
        Guild guild = guildRepository.getOrThrow(guildId);
        if (guild.isOwner(userId)) {
            throw new IllegalStateException("서버 주인은 밴할 수 없습니다.");
        }
        if (guild.isBanned(userId)) {
            throw new IllegalStateException("이미 밴된 유저입니다.");
        }
        guild.addBan(userId);
        guildRepository.save(guild);
    }

    public void removeBan(UUID guildId, UUID userId) {
        Guild guild = guildRepository.getOrThrow(guildId);
        guild.removeBan(userId);
        guildRepository.save(guild);
    }

    public boolean isBanned(UUID guildId, UUID userId) {
        return guildRepository.getOrThrow(guildId).isBanned(userId);
    }

    public Set<UUID> getBannedUsers(UUID guildId) {
        return guildRepository.getOrThrow(guildId).getBannedUserIds();
    }

    public int getBanCount(UUID guildId) {
        return guildRepository.getOrThrow(guildId).getBannedUserIds().size();
    }
}
