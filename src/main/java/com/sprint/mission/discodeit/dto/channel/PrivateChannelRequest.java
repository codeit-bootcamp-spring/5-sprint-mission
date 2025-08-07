package com.sprint.mission.discodeit.dto.channel;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class PrivateChannelRequest {
    private final List<UUID> participantIds;

    public PrivateChannelRequest(List<UUID> participantIds) {
        this.participantIds = participantIds;
    }

}
