package com.sprint.mission.discodeit.dto.request;

<<<<<<< HEAD
=======
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
<<<<<<< HEAD
    List<UUID> participantIds
) {
=======
    @NotEmpty(message = "Name is mandatory") @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters") List<@NotNull UUID> participantIds) {
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)

}
