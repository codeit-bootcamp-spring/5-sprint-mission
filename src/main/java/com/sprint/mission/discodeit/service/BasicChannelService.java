package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.GuildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class BasicChannelService {

    private final ChannelRepository channelRepository;
    private final GuildRepository guildRepository;

    protected void update(UUID id, Consumer<Channel> updater) {
        Channel entity = channelRepository.getOrThrow(id);
        updater.accept(entity);
        channelRepository.save(entity);
    }

    // public Channel create(UUID guildId, String name, ChannelType type) {
    //     Guild guild = guildRepository.getOrThrow(guildId);
    //     Channel channel = channelRepository.save(new Channel(guildId, name, type));
    //     guild.addChannel(channel.getId());
    //     guildRepository.save(guild);
    //     return channel;
    // }
    //
    // public void updateName(UUID channelId, String name) {
    //     update(channelId, c -> c.setName(name));
    // }
    //
    // public void updateType(UUID channelId, ChannelType type) {
    //     update(channelId, c -> c.setType(type));
    // }
    //
    // public void updatePrivate(UUID channelId, boolean isPrivate) {
    //     update(channelId, c -> c.setPrivate(isPrivate));
    // }
    //
    // public void addJoinedUser(UUID channelId, UUID userId) {
    //     update(channelId, c -> c.addUserId(userId));
    // }
    //
    // public void removeJoinedUser(UUID channelId, UUID userId) {
    //     update(channelId, c -> c.removeUserId(userId));
    // }
}
