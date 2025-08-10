package com.sprint.mission.discodeit.service.impl.dev;

import com.sprint.mission.discodeit.domain.entitydev.DevChannel;
import com.sprint.mission.discodeit.domain.entitydev.DevChatRoom;
import com.sprint.mission.discodeit.domain.entitydev.guild.DevGuild;
import com.sprint.mission.discodeit.domain.enums.channel.ChannelType;
import com.sprint.mission.discodeit.repository.devrepository.DevChannelRepository;
import com.sprint.mission.discodeit.repository.devrepository.DevChatRoomRepository;
import com.sprint.mission.discodeit.repository.devrepository.DevGuildRepository;
import com.sprint.mission.discodeit.service.dev.DevChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Profile({"test", "dev"})
public class BasicChannelService implements DevChannelService {

    private final DevChannelRepository channelRepository;
    private final DevGuildRepository guildRepository;
    private final DevChatRoomRepository chatRoomRepository;

    protected void update(UUID id, Consumer<DevChannel> updater) {
        DevChannel entity = channelRepository.getOrThrow(id);
        updater.accept(entity);
        channelRepository.save(entity);
    }

    @Override
    public DevChannel create(UUID guildId, String name, ChannelType type) {
        DevGuild guild = guildRepository.getOrThrow(guildId);
        DevChannel channel = channelRepository.save(new DevChannel(guildId, name, type));
        guild.addChannel(channel.getId());
        guildRepository.save(guild);
        chatRoomRepository.save(new DevChatRoom(channel.getId(), guildId));
        return channel;
    }

    @Override
    public void updateName(UUID channelId, String name) {
        update(channelId, c -> c.setName(name));
    }

    @Override
    public void updateType(UUID channelId, ChannelType type) {
        update(channelId, c -> c.setType(type));
    }

    @Override
    public void updatePrivate(UUID channelId, boolean isPrivate) {
        update(channelId, c -> c.setPrivate(isPrivate));
    }

    @Override
    public void addJoinedUser(UUID channelId, UUID userId) {
        update(channelId, c -> c.addUser(userId));
    }

    @Override
    public void removeJoinedUser(UUID channelId, UUID userId) {
        update(channelId, c -> c.removeUser(userId));
    }
}
