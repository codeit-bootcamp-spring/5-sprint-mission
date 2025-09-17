package com.sprint.mission.discodeit.domain.channel.dto;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    List<UUID> participantIds
) {

}
