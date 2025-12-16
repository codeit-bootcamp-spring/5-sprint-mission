package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;

public record ChannelUpdatedEvent(
    String name,
    ChannelDTO channelDTO
) {

}
