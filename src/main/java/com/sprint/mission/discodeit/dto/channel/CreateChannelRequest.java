package com.sprint.mission.discodeit.dto.channel;

import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ToString
@Getter
public class CreateChannelRequest {
    private final String name;
    private final String description;
    private final List<UUID> participantIds;

    public CreateChannelRequest(String name, String description, List<UUID> participantIds) {
        this.name = name;
        this.description = description;
        this.participantIds = participantIds;
    }

    public CreateChannelRequest(String name, String description) {
        this(name, description, Collections.emptyList());
    }

}
