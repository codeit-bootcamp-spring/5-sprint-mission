package com.sprint.mission.discodeit.dto.request.guild;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GuildCreateRequest(

    @NotNull
    UUID ownerId,

    @NotBlank
    String name,

    boolean discoverable
) {

}
