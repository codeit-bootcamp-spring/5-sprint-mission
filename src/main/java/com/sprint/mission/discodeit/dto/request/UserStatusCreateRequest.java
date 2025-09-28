package com.sprint.mission.discodeit.dto.request;

<<<<<<< HEAD
import java.time.Instant;

public record UserStatusCreateRequest (
        Instant lastActiveAt
){ }
=======
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(@NotBlank(message = "User ID is mandatory") UUID userId,
                                      @NotBlank(message = "Last active at is mandatory") Instant lastActiveAt) {

}
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
