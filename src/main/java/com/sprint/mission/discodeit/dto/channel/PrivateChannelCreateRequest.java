package com.sprint.mission.discodeit.dto.channel;

import static com.sprint.mission.discodeit.support.Constants.MAX_CHANNEL_PARTICIPANTS;
import static com.sprint.mission.discodeit.support.Constants.MIN_CHANNEL_PARTICIPANTS;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

public record PrivateChannelCreateRequest(

    @NotNull
    @Size(min = MIN_CHANNEL_PARTICIPANTS, max = MAX_CHANNEL_PARTICIPANTS)
    Set<@NotNull UUID> participantIds
) {

}
