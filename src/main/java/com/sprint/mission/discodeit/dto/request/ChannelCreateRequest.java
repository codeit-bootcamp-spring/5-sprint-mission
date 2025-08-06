package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import lombok.Builder;

@Builder
public record ChannelCreateRequest(
        String name,
        String description,
        User user
) {
}
