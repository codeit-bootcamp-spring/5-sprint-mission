package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @NotNull
    @Size(min = 2, max = 10)
    Set<@NotNull UUID> participantIds
) {

}
