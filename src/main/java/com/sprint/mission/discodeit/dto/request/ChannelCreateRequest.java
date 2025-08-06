package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.User;
import lombok.Builder;

import java.util.List;

@Builder
public record ChannelCreateRequest(
        String name,
        String description,
        List<User> users
) {
}
