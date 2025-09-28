package com.sprint.mission.discodeit.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank(message = "Content is mandatory") @Size(max = 200, message = "Content must be at most 200 characters") String content,
    @NotBlank(message = "Channel ID is mandatory") UUID channelId,
    @NotBlank(message = "Author ID is mandatory") UUID authorId) {

}
