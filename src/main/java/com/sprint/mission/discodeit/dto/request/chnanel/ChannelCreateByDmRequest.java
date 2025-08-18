package com.sprint.mission.discodeit.dto.request.chnanel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

public record ChannelCreateByDmRequest(

    @NotBlank
    String name,

    @NotNull
    Set<UUID> participants
) {

}
