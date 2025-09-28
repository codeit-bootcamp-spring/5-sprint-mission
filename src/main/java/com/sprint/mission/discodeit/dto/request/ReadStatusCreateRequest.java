package com.sprint.mission.discodeit.dto.request;

<<<<<<< HEAD
import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID userId, UUID channelId
) { }
=======
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(@NotBlank(message = "User ID is mandatory") UUID userId,
                                      @NotBlank(message = "Channel ID is mandatory") UUID channelId,
                                      @NotBlank(message = "Last read at is mandatory") Instant lastReadAt) {

}
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
