package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public class ChannelDto {

    public record create(
            @NotBlank(message = "이름을 입력하세요")
            String name,

            @Schema(description = "채널 상태", implementation = ChannelType.class, allowableValues = {"PUBLIC", "PRIVATE"})
            @NotNull(message = "채널 타입을 입력하세요")
            ChannelType type,
            @Nullable String topic,
            @Nullable String description
    ) {}

    public record createPrivate(
            List<UUID> memberIds
    ) {}

    public record update(
            @NotNull(message = "아이디를 입력해주세요")
            UUID id,
            ChannelType channelType,
            @Nullable String name,
            @Nullable String topic,
            @Nullable String description
    ) {}

    @Builder
    public record response(
            UUID id,
            ChannelType type,
            String name,
            String topic,
            String description,
            String createdAt,
            String updatedAt
    ) {}
}
