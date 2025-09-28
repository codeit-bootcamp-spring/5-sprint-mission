package com.sprint.mission.discodeit.dto.request;

<<<<<<< HEAD
import java.time.Instant;

public record ReadStatusUpdateRequest(
        Instant newLastReadAt
) { }
=======
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public record ReadStatusUpdateRequest(
    @NotBlank(message = "Last read at is mandatory") Instant newLastReadAt) {

}
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
