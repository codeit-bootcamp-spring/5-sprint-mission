package com.sprint.mission.discodeit.dto.channel;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CreateChannelRequest {
    private final String name;
    private final String description;
    private final List<UUID> participantIds;

    public CreateChannelRequest(String name, String description, List<UUID> participantIds) {
        this.name = name;
        this.description = description;
        this.participantIds = participantIds;
    }

    // public 채널용 편의 생성자
    public CreateChannelRequest(String name, String description) {
        this(name, description, Collections.emptyList());
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<UUID> getParticipantIds() { return participantIds; }
}
