package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.enums.Permission;
import com.sprint.mission.discodeit.enums.channel.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.GuildService;

import java.util.Set;
import java.util.UUID;

public class FileChannelService extends BaseFileService<Channel> implements ChannelService {
    private final GuildService guildService;

    public FileChannelService(GuildService guildService) {
        super("channels.ser");
        this.guildService = guildService;
    }

    @Override
    public Channel save(Channel channel) {
        if (existsById(channel.getId())) {
            throw new IllegalArgumentException("중복된 id가 존재합니다.");
        }

        Guild guild = guildService.getOrThrow(channel.getGuildId());
        channel.setPermissionsToUser(guild.getOwnerId(), Set.of(Permission.ADMINISTRATOR));

        return super.save(channel);
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
    public void updatePublic(UUID channelId, boolean isPublic) {
        update(channelId, c -> c.setPublic(isPublic));
    }

    @Override
    public void addJoinedUser(UUID channelId, UUID userId) {
        update(channelId, c -> c.addJoinedUser(userId));
    }

    @Override
    public void removeJoinedUser(UUID channelId, UUID userId) {
        update(channelId, c -> c.removeJoinedUser(userId));
    }
}
