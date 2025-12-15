package com.sprint.mission.discodeit.channel.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

@Schema(
    example = """
        {
          "participantIds": [
            "dd210d1a-ebe6-499f-8936-859790fd3716",
            "8fb5dd71-b7a0-4b5d-bf37-ea410473c618"
            ]
        }
        """
)
public record PrivateChannelCreateRequest(
    @NotNull
    @Size(min = 2, max = 10)
    Set<UUID> participantIds
) {
}
