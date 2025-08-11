package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;

public record ChannelDto() {
    public record CreateChannel(String name, ChannelType type, String description) { }
}
