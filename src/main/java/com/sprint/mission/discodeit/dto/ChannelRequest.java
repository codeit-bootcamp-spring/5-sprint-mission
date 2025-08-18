package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public class ChannelRequest {

    public record create(
            @NotBlank(message = "이름을 입력하세요")
            String name,
            @Nullable String topic,
            @Nullable String description
    ) {}

    public record createPrivate(
            @NotBlank(message = "이름을 입력하세요")
            String name,
            @NotEmpty(message = "멤버를 1명 이상 추가하세요")
            List<UUID> memberIds
    ) {}

    public record update(
            @NotNull(message = "아이디를 입력해주세요")
            UUID id,
            @Nullable String name,
            @Nullable String topic,
            @Nullable String description
    ) {}

    public record join(
            @NotNull(message = "아이디를 입력해주세요")
            UUID userId,
            @NotNull(message = "아이디를 입력해주세요")
            UUID channelId
    ){}
}
