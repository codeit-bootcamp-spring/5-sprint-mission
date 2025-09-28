package com.sprint.mission.discodeit.dto.request;

<<<<<<< HEAD
import java.time.Instant;

public record UserStatusUpdateRequest(
        Instant newLastActiveAt
) {
=======
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record UserStatusUpdateRequest(
    @NotBlank(message = "User ID is mandatory") Instant newLastActiveAt) {
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)

}
