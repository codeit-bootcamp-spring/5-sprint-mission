package com.codeit.mission.discodeit.dto.channel;

import com.codeit.mission.discodeit.entity.ChannelType;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PublicChannelCreateRequest {

    private final String name;
    private final String description;

    public PublicChannelCreateRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ChannelType getType() {
        return ChannelType.PUBLIC;
    }
}
