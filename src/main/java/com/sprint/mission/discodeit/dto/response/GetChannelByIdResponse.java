package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Channel;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetChannelByIdResponse(
    Channel channel,
    Instant recentMessageTime,
    List<UUID> existingUsersIds
) {

}

