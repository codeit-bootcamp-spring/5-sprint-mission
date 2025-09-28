package com.sprint.mission.discodeit.dto.request;

<<<<<<< HEAD
import java.util.UUID;

public record MessageUpdateRequest(
        UUID messageId,
        String newContent
) { }
=======
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageUpdateRequest(
    @NotBlank(message = "Content is mandatory") @Size(max = 200, message = "Content must be at most 200 characters") String newContent) {

}
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
