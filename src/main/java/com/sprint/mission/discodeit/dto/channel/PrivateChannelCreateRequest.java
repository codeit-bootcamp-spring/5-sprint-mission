package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @NotNull
    Set<@NotNull UUID> participantIds
) {

}
