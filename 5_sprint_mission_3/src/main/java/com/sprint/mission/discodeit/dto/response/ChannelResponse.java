package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public interface ChannelResponse {
    UUID id();
    String name();
    ChannelType type();
}
