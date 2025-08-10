package com.codeit.mission.discodeit.dto.channel;

import com.codeit.mission.discodeit.entity.ChannelType;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class PrivateChannelCreateRequest {

    private final List<UUID> participantUserIds;

    public PrivateChannelCreateRequest(List<UUID> participantUserIds) {
        this.participantUserIds = participantUserIds;
    }

    public ChannelType getType() {
        return ChannelType.PRIVATE;
    }

    public String getName() {
        return null;
    }

    public String getDescription() {
        return null;
    }
}
