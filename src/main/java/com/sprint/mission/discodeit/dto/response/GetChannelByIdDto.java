package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetChannelByIdDto(
        Channel channel,
        Instant recentMessageTime,
        List<UUID> existingUsersIds
) {}
