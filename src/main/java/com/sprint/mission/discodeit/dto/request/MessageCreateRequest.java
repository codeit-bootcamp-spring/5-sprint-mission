package com.sprint.mission.discodeit.dto.request;

<<<<<<< HEAD
import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId
) {}
=======
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank(message = "Content is mandatory") @Size(max = 200, message = "Content must be at most 200 characters") String content,
    @NotBlank(message = "Channel ID is mandatory") UUID channelId,
    @NotBlank(message = "Author ID is mandatory") UUID authorId) {

}
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
