package com.sprint.mission.discodeit.dto.readstatus;

import java.util.UUID;

public record ChannelUnreadStatusDto(
        UUID channelId,
        boolean hasUnread
) {}
