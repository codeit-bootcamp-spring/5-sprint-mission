package com.sprint.mission.discodeit.service.channel;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.Guild;
import com.sprint.mission.discodeit.domain.enums.ChannelType;
import com.sprint.mission.discodeit.dto.request.chnanel.ChannelCreateByDmRequest;
import com.sprint.mission.discodeit.dto.request.chnanel.ChannelCreateByGuildRequest;
import com.sprint.mission.discodeit.dto.request.chnanel.ChannelUpdateByGuildRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.GuildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelService {

    private static final int DM_MIN = 2;
    private static final int DM_MAX = 10;

    private final ChannelRepository channelRepository;
    private final GuildRepository guildRepository;

    @Transactional
    protected void update(UUID id, Consumer<Channel> updater) {
        Channel entity = channelRepository.getOrThrow(id);
        updater.accept(entity);
        channelRepository.save(entity);
    }

    private static void require(boolean cond, String msg) {
        if (!cond) throw new IllegalStateException(msg);
    }

    @Transactional
    public ChannelResponse createGuildChannel(ChannelCreateByGuildRequest req) {
        Guild guild = guildRepository.getOrThrow(req.guildId());

        if (Boolean.TRUE.equals(req.isSecret())) {
            require(req.allowedMemberIds() != null && !req.allowedMemberIds().isEmpty(),
                    "Secret guild channel requires at least one allowed member.");
        } else {
            require(req.allowedMemberIds() == null || req.allowedMemberIds().isEmpty(),
                    "Public guild channel must not have allowed members.");
        }

        Channel ch = Channel.createGuildChannel(
                req.guildId(),
                req.name(),
                Objects.requireNonNull(req.type(), "type must not be null"),
                req.isSecret(),
                (req.allowedMemberIds() == null) ? Set.of() : req.allowedMemberIds()
        );

        Channel saved = channelRepository.save(ch);
        guild.addChannel(saved.getId());
        guildRepository.save(guild);

        return ChannelMapper.toChannelResponse(saved);
    }

    @Transactional
    public ChannelResponse createDmChannel(ChannelCreateByDmRequest req) {
        Set<UUID> participants = Objects.requireNonNull(req.participants(), "participants must not be null");
        require(participants.size() >= DM_MIN && participants.size() <= DM_MAX,
                "DM requires 2 to 10 participants.");

        Channel ch = Channel.createDm(
                req.name(),
                ChannelType.CHAT,
                participants
        );
        Channel saved = channelRepository.save(ch);
        return ChannelMapper.toChannelResponse(saved);
    }

    @Transactional
    public void updateGuildChannel(UUID channelId, ChannelUpdateByGuildRequest req) {
        Channel ch = channelRepository.getOrThrow(channelId);
        require(ch.isGuildChannel(), "Not a guild channel.");
        if (req.name() != null && !req.name().isBlank()) {
            ch.setName(req.name());
        }
        if (req.type() != null) {
            ch.setType(req.type());
        }
        if (ch.isSecretGuildChannel() && req.allowedMemberIds() != null) {
            Set<UUID> target = new HashSet<>(req.allowedMemberIds());
            Set<UUID> current = new HashSet<>(ch.getMemberIds());
            for (UUID uid : current) {
                if (!target.contains(uid)) {
                    ch.removeMember(uid);
                }
            }
            for (UUID uid : target) {
                if (!current.contains(uid)) {
                    ch.addMember(uid);
                }
            }
        }
        channelRepository.save(ch);
    }

    @Transactional
    public void deleteGuildChannel(UUID channelId, UUID actorId) {
        Channel ch = channelRepository.getOrThrow(channelId);
        require(ch.isGuildChannel(), "Not a guild channel.");

        UUID guildId = ch.getGuildId();
        Guild guild = guildRepository.getOrThrow(guildId);

        channelRepository.softDeleteById(channelId);
        guild.removeChannel(channelId);
        guildRepository.save(guild);
    }

    @Transactional
    public void joinDm(UUID channelId, UUID userId) {
        Channel ch = channelRepository.getOrThrow(channelId);
        require(ch.isPrivate(), "Not a DM channel.");
        int size = ch.getMemberIds().size();
        require(size >= 3, "Only DM with 3+ members can invite users.");
        require(size < DM_MAX, "DM member limit exceeded.");

        ch.addMember(Objects.requireNonNull(userId, "userId must not be null"));
        channelRepository.save(ch);
    }

    @Transactional
    public void leaveDm(UUID channelId, UUID userId) {
        Channel ch = channelRepository.getOrThrow(channelId);
        require(ch.isPrivate(), "Not a DM channel.");
        int size = ch.getMemberIds().size();
        require(size >= 3, "Only DM with 3+ members can leave.");

        ch.removeMember(Objects.requireNonNull(userId, "userId must not be null"));
        channelRepository.save(ch);
    }

    public List<ChannelResponse> findDmChannelsForUser(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        return channelRepository.findAll().stream()
                .filter(Channel::isPrivate)
                .filter(ch -> ch.isMember(userId))
                .map(ChannelMapper::toChannelResponse)
                .toList();
    }

    public List<ChannelResponse> findGuildChannels(UUID guildId, UUID userId) {
        List<Channel> publics = channelRepository.findAllPublicByGuildId(guildId);
        List<Channel> secrets = channelRepository.findAllSecretByGuildIdAndMember(guildId, userId);

        return Collections.unmodifiableList(
                        new ArrayList<Channel>() {{
                            addAll(publics);
                            addAll(secrets);
                        }})
                .stream()
                .map(ChannelMapper::toChannelResponse)
                .toList();
    }
}
