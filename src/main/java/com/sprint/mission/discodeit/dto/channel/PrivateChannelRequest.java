package com.sprint.mission.discodeit.dto.channel;

import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@ToString
@Getter
public class PrivateChannelRequest {
    private final List<UUID> participantIds;

    public PrivateChannelRequest(List<UUID> participantIds) {
        this.participantIds = participantIds;
    }
}
