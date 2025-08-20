package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public class ChannelRequest {

    public record Create(
            @NotBlank(message = "이름을 입력하세요")
            String name,
            @Nullable String description
    ) {}

    public record CreatePrivate(
            @NotEmpty(message = "멤버를 1명 이상 추가하세요")
            List<UUID> participantIds
    ) {}

    public record Update(
            String newName,
            String newDescription
    ) {}

    public record join(
            @NotNull(message = "아이디를 입력해주세요")
            UUID userId,
            @NotNull(message = "아이디를 입력해주세요")
            UUID channelId
    ){}
}
